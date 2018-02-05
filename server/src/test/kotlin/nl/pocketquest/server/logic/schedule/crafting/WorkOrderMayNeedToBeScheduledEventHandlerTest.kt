package nl.pocketquest.server.logic.schedule.crafting

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.crafting.WorkOrderQueue
import nl.pocketquest.server.api.user.UserCraftingCountRoute
import nl.pocketquest.server.api.user.UserMaxCraftingCountRoute
import nl.pocketquest.server.logic.events.Event
import org.junit.Assert.*
import org.junit.Test

class WorkOrderMayNeedToBeScheduledEventHandlerTest : WorkOrderBaseTest() {

    @Test
    fun shouldTakeFirstOrder() {
        val maxCrafting = db.get<Long>(UserMaxCraftingCountRoute("chocolate_bear").route)
        val crafting = db.get<Long>(UserCraftingCountRoute("chocolate_bear").route)
        val queue = WorkOrderQueue.of("chocolate_bear", kodein)
        runBlocking {
            queue.submit("workorder_2", 850L)
            queue.submit("workorder_1", 750L)
            crafting.content = 0L
            maxCrafting.content = 1L
            WorkOrderMayNeedToBeScheduledEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                            WorkOrderUserData("chocolate_bear"),
                            1000L
                    )
            )
            assertEquals(1L, crafting.content!!)
            assertTrue(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_SCHEDULED,
                                    WorkOrderData("chocolate_bear", "workorder_1"),
                                    1000L
                            )
                    )
            )
            //User is crafting max items
            assertFalse(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                                    WorkOrderUserData("chocolate_bear"),
                                    1000L
                            )
                    )
            )
        }

    }

    @Test
    fun shouldTakeFirstAndRecheck() {
        val maxCrafting = db.get<Long>(UserMaxCraftingCountRoute("chocolate_bear").route)
        val crafting = db.get<Long>(UserCraftingCountRoute("chocolate_bear").route)
        val queue = WorkOrderQueue.of("chocolate_bear", kodein)
        runBlocking {
            queue.submit("workorder_2", 850L)
            queue.submit("workorder_1", 750L)
            crafting.content = 0L
            maxCrafting.content = 2L
            WorkOrderMayNeedToBeScheduledEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                            WorkOrderUserData("chocolate_bear"),
                            1000L
                    )
            )
            assertEquals(1L, crafting.content!!)
            assertTrue(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_SCHEDULED,
                                    WorkOrderData("chocolate_bear", "workorder_1"),
                                    1000L
                            )
                    )
            )
            //User is not crafting max items
            assertTrue(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                                    WorkOrderUserData("chocolate_bear"),
                                    1000L
                            )
                    )
            )
        }
    }

    @Test
    fun shouldHandleEmptyQue() {
        db.clear()
        runBlocking {
            WorkOrderMayNeedToBeScheduledEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                            WorkOrderUserData("chocolate_bear"),
                            1000L
                    )
            )
            assertTrue(eventPool.empty())
        }
    }

    @Test
    fun shouldHandleMaxCrafting() {
        val maxCrafting = db.get<Long>(UserMaxCraftingCountRoute("chocolate_bear").route)
        val crafting = db.get<Long>(UserCraftingCountRoute("chocolate_bear").route)
        maxCrafting.content = 1L
        crafting.content = 1L
        val queue = WorkOrderQueue.of("chocolate_bear", kodein)
        runBlocking {
            queue.submit("workorder_2", 850L)
            WorkOrderMayNeedToBeScheduledEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                            WorkOrderUserData("chocolate_bear"),
                            1000L
                    )
            )
            assertTrue(eventPool.empty())
        }

    }
}