package nl.pocketquest.pocketquest.views.main.workorder

import nl.pocketquest.pocketquest.game.crafting.WorkOrder
import nl.pocketquest.pocketquest.views.BasePresenter
import nl.pocketquest.pocketquest.views.BaseView

class WorkOrderContract {

    interface WorkOrderView : BaseView {
        fun initialize(workOrders: Collection<WorkOrder>)

        fun setLoading(loading: Boolean)

        fun addWorkOrder(workOrder: WorkOrder)

        fun removeWorkOrder(workOrder: WorkOrder)

        fun updateWorkOrder(workOrder: WorkOrder, newWorkOrder: WorkOrder)
    }

    abstract class WorkOrderPresenter(workOrderView: WorkOrderView) : BasePresenter<WorkOrderView>(workOrderView) {

        abstract fun onCancelWorkOrder(workOrder: WorkOrder)

        abstract fun onClaimWorkOrder(workOrder: WorkOrder)
    }
}
