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
            val now = System.currentTimeMillis()

            workOrderView.initialize(listOf(
                    WorkOrder("axe_1", 2, WorkOrderStatus.Finished(now - 1000)),
                    WorkOrder("axe_1", 1, WorkOrderStatus.Finished(now - 2000)),
                    WorkOrder("axe_1", 4, WorkOrderStatus.Active(now - 1000, now + 13000)),
                    WorkOrder("axe_1", 3, WorkOrderStatus.Active(now - 2000, now + 12000)),
                    WorkOrder("axe_1", 6, WorkOrderStatus.Submitted(now - 1000)),
                    WorkOrder("axe_1", 5, WorkOrderStatus.Submitted(now - 2000))
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
