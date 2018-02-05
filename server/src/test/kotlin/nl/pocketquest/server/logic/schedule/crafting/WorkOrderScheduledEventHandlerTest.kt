package nl.pocketquest.server.logic.schedule.crafting

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.user.UserCraftingCountRoute
import nl.pocketquest.server.api.user.UserMaxCraftingCountRoute
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.testhelpers.MockDataSource
import org.junit.Assert.*
import org.junit.Test

class WorkOrderScheduledEventHandlerTest : WorkOrderBaseTest() {

    @Test
    fun shouldBeActive() {
        db.clear()
        eventPool.clear()
        val (workorderId, workorder) = storeWorkOrder()
        workorder.content!!.count = 3
        val craftingCountRoute = UserCraftingCountRoute("chocolate_bear")
        val craftingCount = MockDataSource(1L)
        db.add(craftingCountRoute.route, craftingCount)
        runBlocking {
            WorkOrderScheduledEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_SCHEDULED,
                            WorkOrderData(
                                    "chocolate_bear", workorderId
                            ),
                            1500L
                    )
            )
            assertEquals(1L, craftingCount.content!!)
            assertTrue(workorder.content!!.active)
            assertEquals(1500L, workorder.content!!.started_at)
            assertEquals(13500L, workorder.content!!.finished_at)
            assertTrue(eventPool.contains(Event.of(
                    WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                    WorkOrderProcessData(
                            "chocolate_bear", workorderId, 4000L
                    ), 5500L
            )))
        }
    }
}