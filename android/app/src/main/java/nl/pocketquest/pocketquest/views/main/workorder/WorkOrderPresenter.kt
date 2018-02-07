package nl.pocketquest.pocketquest.views.main.workorder

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.crafting.WorkOrder
import nl.pocketquest.pocketquest.game.entities.WorkOrderRequester
import nl.pocketquest.pocketquest.game.player.*
import nl.pocketquest.pocketquest.utils.getTimeOfset
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.custom.async
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

        async(CommonPool){
            workOrderView.setTimeOffset(getTimeOfset())
        }
        wtf("detach")
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
        whenLoggedIn {
            WorkOrderRequester.cancelWorkorder(it.uid, workOrder.id)
        }
//        workOrderView.removeWorkOrder(workOrder)
    }

    override fun onClaimWorkOrder(workOrder: WorkOrder) {
        info { "Workorder claimed: $workOrder" }
        whenLoggedIn {
            WorkOrderRequester.claimWorkorder(it.uid, workOrder.id)
        }
        workOrderView.removeWorkOrder(workOrder)
    }
}
