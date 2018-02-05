package nl.pocketquest.pocketquest.views.main.workorder

import nl.pocketquest.pocketquest.game.crafting.WorkOrder
import nl.pocketquest.pocketquest.game.player.WorkOrderList
import nl.pocketquest.pocketquest.game.player.WorkOrderUpdateListener
import nl.pocketquest.pocketquest.game.player.WorkOrdersInitializedListener
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.info
import org.jetbrains.anko.wtf

class WorkOrderPresenter(
        private val workOrderView: WorkOrderContract.WorkOrderView
) : WorkOrderContract.WorkOrderPresenter(workOrderView)
        , WorkOrderUpdateListener
        , WorkOrdersInitializedListener {
    private var hasLoaded = false
    private var workOrderList: WorkOrderList? = null


    override fun onDetach() {
        workOrderList?.removeWorkOrderListener(this)
        workOrderList?.removeWorkOrderInitializedListeners(this)
    }

    override fun onAttach() {
        workOrderView.setLoading(true)
        whenLoggedIn {
            workOrderList = WorkOrderList.getUserWorkOrderList(it.uid).also {
                it.addWorkOrderListener(this)
                it.addWorkOrderInitializedListeners(this)
            }
        }
    }


    override fun initialized(workOrders: Collection<WorkOrder>) {
        hasLoaded = true
        wtf("initialize $workOrders")
        workOrderView.initialize(workOrders)
        workOrderView.setLoading(false)
        workOrderList?.removeWorkOrderInitializedListeners(this)
    }


    override fun onUpdate(oldWorkOrder: WorkOrder?, newWorkOrder: WorkOrder?) {
        if (hasLoaded) {

            wtf("$oldWorkOrder->$newWorkOrder")
            when {
                oldWorkOrder == null && newWorkOrder != null -> workOrderView.addWorkOrder(newWorkOrder)
                oldWorkOrder != null && newWorkOrder != null -> workOrderView.updateWorkOrder(oldWorkOrder, newWorkOrder)
                oldWorkOrder != null && newWorkOrder == null -> workOrderView.removeWorkOrder(oldWorkOrder)
                else -> wtf("what kind of an update is this?: $oldWorkOrder to $newWorkOrder")
            }
        }
    }

    override fun onCancelWorkOrder(workOrder: WorkOrder) {
        info { "Workorder cancelled: $workOrder" }

        workOrderView.removeWorkOrder(workOrder)
    }

    override fun onClaimWorkOrder(workOrder: WorkOrder) {
        info { "Workorder claimed: $workOrder" }

        workOrderView.removeWorkOrder(workOrder)
    }
}
