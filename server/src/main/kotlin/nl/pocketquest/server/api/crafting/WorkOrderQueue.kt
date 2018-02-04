package nl.pocketquest.server.api.crafting

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.dataaccesslayer.TransactionResult

data class WorkOrderQueueModel(
        val queue: MutableMap<String, Long> = mutableMapOf()
)

data class WorkOrderQueRoute(private val userId: String) : Findable<WorkOrderQueueModel> {
    override val route = listOf("workorder_queues", userId)
    override val expectedType = WorkOrderQueueModel::class.java
}

class WorkOrderQueue internal constructor(private val queueRef: DataSource<WorkOrderQueueModel>) {

    suspend fun submit(workOrderID: String, timeStamp: Long) = queueRef.transaction {
        val current = it ?: WorkOrderQueueModel()
        current.queue[workOrderID] = timeStamp
        TransactionResult.success(current)
    }

    suspend fun delete(workOrderID: String) = queueRef.transaction {
        when {
            it == null -> TransactionResult.success(null)
            !it.queue.containsKey(workOrderID) -> TransactionResult.abort()
            else -> {
                it.queue -= workOrderID
                TransactionResult.success(it)
            }
        }
    }

    suspend fun takeOldest(): String? {
        var oldestWorkOrder: String? = null
        queueRef.transaction {
            val current = it ?: WorkOrderQueueModel()
            val oldest = current.queue.values.sorted().firstOrNull()
            val oldestKey = current.queue.filterValues { it == oldest }.keys.firstOrNull()
            oldestKey?.also(current.queue::minusAssign)
            oldestWorkOrder = oldestKey
            TransactionResult.success(current)
        }
        return oldestWorkOrder
    }

    companion object {
        fun of(userId: String, kodein: Kodein) = WorkOrderQueue(
                kodein.instance<Database>()
                        .resolver
                        .resolve(WorkOrderQueRoute(userId))
        )
    }
}