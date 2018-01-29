package nl.pocketquest.pocketquest.views.main.workorder

import nl.pocketquest.pocketquest.game.crafting.WorkOrder
import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView

class WorkOrderContract {

    interface WorkOrderView : BaseView {
        fun initialize(workOrders: List<WorkOrder>)

        fun addWorkOrder(workOrder: WorkOrder)

        fun removeWorkOrder(workOrder: WorkOrder)

        fun updateWorkOrder(workOrder: WorkOrder, newWorkOrder: WorkOrder)
    }

    abstract class WorkOrderPresenter(workOrderView: WorkOrderView) : BasePresenter<WorkOrderView>(workOrderView) {
        abstract fun onAttached()

        abstract fun onCancelWorkOrder(workOrder: WorkOrder)

        abstract fun onClaimWorkOrder(workOrder: WorkOrder)
    }
}
