package nl.pocketquest.pocketquest.game.player

import com.google.firebase.database.*
import nl.pocketquest.pocketquest.game.crafting.WorkOrder
import nl.pocketquest.pocketquest.game.crafting.WorkOrderStatus
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.getValue
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.wtf
import kotlin.properties.Delegates.observable

interface OnWorkOrderStateChanged {
    fun isSubmitted(workOrder: WorkOrder)
    fun isStarted(workOrder: WorkOrder)
    fun isFinished(workOrder: WorkOrder)
    fun isRemoved(workOrder: WorkOrder)
}

interface WorkOrderUpdateListener {
    fun onUpdate(oldWorkOrder: WorkOrder?, newWorkOrder: WorkOrder?)
}

interface WorkOrdersInitializedListener {
    fun initialized(workOrders: Collection<WorkOrder>)
}

class WorkOrderList(private val ref: DatabaseReference) : AnkoLogger {
    private val workOrders = mutableMapOf<String, FBWorkOrderModel>()
    private val simpleWorkOrders get() = workOrders.values.map { it.toWorkOrder() }
    private val workOrderUpdater = WorkOrderUpdater(this)
    private val workOrderListeners = mutableListOf<OnWorkOrderStateChanged>()
    private val workOrderUpdateListeners = mutableListOf<WorkOrderUpdateListener>()
    private val workOrderInitializedListener = mutableListOf<WorkOrdersInitializedListener>()
    private var initialLoad by observable(false) { _, _, new ->
        if (new) {
            workOrderInitializedListener.forEach {
                it.initialized(simpleWorkOrders)
            }
            workOrderInitializedListener.clear()
            someOneListening = checkSomeoneIsListening()
        }
    }

    fun getRichWorkOrder(workorder: WorkOrder) = workOrders[workorder.id].also {
        wtf("$it in $workOrders")
    }

    private var someOneListening by observable(false) { _, old, new ->
        if (old != new) {
            if (new) start() else cleanup()
        }
    }

    private fun start() {
        ref.addChildEventListener(workOrderUpdater)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = Unit

            override fun onDataChange(p0: DataSnapshot?) {
                initialLoad = true
            }
        })
    }

    private fun cleanup() {
        ref.removeEventListener(workOrderUpdater)
        workOrders.clear()
        initialLoad = false
    }

    fun addWorkOrderListener(listener: OnWorkOrderStateChanged) {
        workOrderListeners += listener
        someOneListening = true
    }

    fun addWorkOrderListener(listener: WorkOrderUpdateListener) {
        wtf("added")
        workOrderUpdateListeners += listener
        someOneListening = true
    }

    fun addWorkOrderInitializedListeners(listener: WorkOrdersInitializedListener) {
        if (initialLoad) listener.initialized(simpleWorkOrders) else workOrderInitializedListener += listener
        someOneListening = true
    }

    fun checkSomeoneIsListening() = (workOrderListeners.size + workOrderUpdateListeners.size + workOrderInitializedListener.size) != 0
    fun removeWorkOrderListener(listener: OnWorkOrderStateChanged) {
        workOrderListeners -= listener
        someOneListening = checkSomeoneIsListening()
    }

    fun removeWorkOrderListener(listener: WorkOrderUpdateListener) {
        wtf("removed!!!")
        workOrderUpdateListeners -= listener
        someOneListening = checkSomeoneIsListening()
    }

    fun removeWorkOrderInitializedListeners(listener: WorkOrdersInitializedListener) {
        workOrderInitializedListener -= listener
        someOneListening = checkSomeoneIsListening()
    }

    private fun List<OnWorkOrderStateChanged>.notifyOf(workOrder: WorkOrder) = when (workOrder.status) {
        is WorkOrderStatus.Submitted -> forEach { it.isSubmitted(workOrder) }
        is WorkOrderStatus.Active -> forEach { it.isStarted(workOrder) }
        is WorkOrderStatus.Finished -> forEach { it.isFinished(workOrder) }
    }

    fun removeWorkOrder(fbWorkOrder: FBWorkOrderModel) {
        workOrders.remove(fbWorkOrder.id)
        workOrderListeners.forEach { it.isRemoved(fbWorkOrder.toWorkOrder()) }
        workOrderUpdateListeners.forEach { it.onUpdate(fbWorkOrder.toWorkOrder(), null) }
    }

    fun updateWorkOrder(newWorkorder: FBWorkOrderModel) {
        val oldWorkOrder = workOrders[newWorkorder.id]
        wtf("$oldWorkOrder:${oldWorkOrder?.status}->$${newWorkorder.status}")
        if (oldWorkOrder?.status != newWorkorder.status) {
            workOrderUpdateListeners.forEach { it.onUpdate(oldWorkOrder?.toWorkOrder(), newWorkorder.toWorkOrder()) }
            workOrderListeners.notifyOf(newWorkorder.toWorkOrder())
            workOrders[newWorkorder.id] = newWorkorder
        }
    }

    companion object {
        private var userWorkOrderList: WorkOrderList? = null
        fun getUserWorkOrderList(uid: String): WorkOrderList {
            return userWorkOrderList ?: WorkOrderList(DATABASE.getReference("work_orders/$uid"))
                    .also { userWorkOrderList = it }
        }
    }
}

data class FBWorkOrderModel(
        var id: String = "",
        var count: Int = 0,
        var completed: Long = 0,
        var recipe: String = "",
        var active: Boolean = false,
        var started_at: Long = 0,
        var finished_at: Long = 0,
        var submitted_at: Long = 0,
        var finished: Boolean = false
) {
    val status
        get() = when {
            finished -> WorkOrderStatus.Finished(finished_at)
            active -> WorkOrderStatus.Active(started_at, finished_at)
            else -> WorkOrderStatus.Submitted(submitted_at)
        }

    fun toWorkOrder() = WorkOrder(id, recipe, count, status)
}

class WorkOrderUpdater(val workOrderList: WorkOrderList) : ChildEventListener, AnkoLogger {

    fun DataSnapshot.toWorkOrder() = getValue<FBWorkOrderModel>()?.also { it.id = key }
    override fun onCancelled(p0: DatabaseError?) = Unit

    override fun onChildMoved(snapshot: DataSnapshot, oldKey: String?) = Unit

    override fun onChildChanged(snapshot: DataSnapshot, oldKey: String?) {
        snapshot.toWorkOrder()?.also {
            workOrderList.updateWorkOrder(it)
        }
    }

    override fun onChildAdded(snapshot: DataSnapshot, oldKey: String?) {
        snapshot.toWorkOrder()?.also {
            workOrderList.updateWorkOrder(it)
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        snapshot.toWorkOrder()?.also {
            workOrderList.removeWorkOrder(it)
        }
    }
}
