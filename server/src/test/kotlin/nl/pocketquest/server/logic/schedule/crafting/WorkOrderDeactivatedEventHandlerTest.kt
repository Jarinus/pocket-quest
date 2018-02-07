package nl.pocketquest.server.logic.schedule.crafting

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.crafting.WorkOrderQueue
import nl.pocketquest.server.api.user.UserCraftingCountRoute
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.testhelpers.MockDataSource
import org.junit.Assert.*
import org.junit.Test

class WorkOrderDeactivatedEventHandlerTest : WorkOrderBaseTest() {
    @Test
    fun shouldDeactivateWorkOrder() {
        val (workOrderId, workOrderData) = storeWorkOrder()
        workOrderData.content!!.active = true
        workOrderData.content!!.finished = false
        val craftingData = MockDataSource(1L)
        db.add(UserCraftingCountRoute("chocolate_bear").route, craftingData)
        val queue = WorkOrderQueue.of("chocolate_bear", kodein)
        runBlocking {
            queue.submit(workOrderId, 2000L)
            WorkOrderDeactivatedEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_DEACTIVATED,
                            WorkOrderDeactivationData("chocolate_bear", workOrderId),
                            1000L
                    )
            )
            assertNull(queue.takeOldest())
            assertEquals(0L, craftingData.content)
            assertEquals(false, workOrderData.content!!.active)
            assertEquals(true, workOrderData.content!!.finished)

        }
    }
}