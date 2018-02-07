package nl.pocketquest.server.api.crafting

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.dataaccesslayer.TransactionResult
import kotlin.concurrent.fixedRateTimer

internal data class WorkOrderModel(
        var count: Long = 0,
        var completed: Long = 0,
        var recipe: String = "",
        var active: Boolean = false,
        var started_at: Long = 0,
        var submitted_at: Long = 0,
        var finished_at: Long = 0,
        var finished: Boolean = false
)

internal class WorkOrderCollectionRoute(userId: String) : Findable<WorkOrderModel> {
    override val route = listOf("work_orders", userId)
    override val expectedType = WorkOrderModel::class.java
}

internal class WorkOrderRoute(userId: String, workOrderID: String) : Findable<WorkOrderModel> {
    override val route = WorkOrderCollectionRoute(userId).route + listOf(workOrderID)
    override val expectedType = WorkOrderModel::class.java
}

class WorkOrder internal constructor(val model: DataSource<WorkOrderModel>) {

    suspend fun count() = model.readAsync()?.count

    suspend fun completed() = model.readAsync()?.completed

    suspend fun recipe() = model.readAsync()?.recipe

    suspend fun active() = model.readAsync()?.active

    suspend fun startedAt() = model.readAsync()?.started_at

    suspend fun delete() = model.delete()

    suspend fun exists() = model.readAsync() != null

    suspend fun finished(): Boolean {
        val completed = completed()
        return completed != null && completed == count()
    }

    suspend fun addOneCompleted() = model.transaction {
        val current = it ?: return@transaction TransactionResult.success<WorkOrderModel>(null)
        if (current.completed < current.count && current.active) {
            current.completed++
            TransactionResult.success(current)
        } else {
            TransactionResult.abort()
        }
    }

    private suspend fun updateObject(transformer: (WorkOrderModel) -> Unit) = model.transaction {
        val current = it ?: return@transaction TransactionResult.success<WorkOrderModel>(null)
        transformer(current)
        TransactionResult.success(current)
    }

    suspend fun updateObject(
            active: Boolean? = null,
            finished: Boolean? = null,
            startedAt: Long? = null,
            finishedAt: Long? = null
    ) = updateObject {
        active?.apply { it.active = this }
        finished?.apply { it.finished = this }
        startedAt?.apply { it.started_at = this }
        finishedAt?.apply { it.finished_at = this }

    }

    companion object {
        fun byId(userId: String, workOrderID: String, kodein: Kodein) = WorkOrder(
                kodein.instance<Database>()
                        .resolver
                        .resolve(WorkOrderRoute(userId, workOrderID))
        )

        /**
         * Creates the workOrder specified by the contents and returns the id of the new workOrder
         */
        suspend fun submit(userId: String, count: Long, recipe: String, submittedAt: Long, kodein: Kodein): String =
                kodein.instance<Database>().collection(WorkOrderCollectionRoute(userId))
                        .add(WorkOrderModel(
                                count = count,
                                recipe = recipe,
                                submitted_at = submittedAt
                        ))
    }
}