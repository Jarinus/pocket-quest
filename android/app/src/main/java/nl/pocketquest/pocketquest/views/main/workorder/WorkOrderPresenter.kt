package nl.pocketquest.pocketquest.views.main.workorder

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.crafting.WorkOrder
import nl.pocketquest.pocketquest.game.crafting.WorkOrderStatus
import org.jetbrains.anko.info

class WorkOrderPresenter(private val workOrderView: WorkOrderContract.WorkOrderView)
    : WorkOrderContract.WorkOrderPresenter(workOrderView) {

    override fun onAttached() {
        workOrderView.setLoading(true)

        async(CommonPool) {
            //TODO: Implement Firebase functionality
            workOrderView.initialize(listOf(
                    WorkOrder("axe_1", 5, WorkOrderStatus.Active(12313231130, 12313231132)),
                    WorkOrder("axe_1", 10, WorkOrderStatus.Submitted(12313231131))
            ))

            workOrderView.setLoading(false)
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
