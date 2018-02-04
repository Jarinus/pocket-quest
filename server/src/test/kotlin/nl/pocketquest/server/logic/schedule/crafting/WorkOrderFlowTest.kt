package nl.pocketquest.server.logic.schedule.crafting

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.Server
import nl.pocketquest.server.api.crafting.WorkOrder
import nl.pocketquest.server.api.crafting.WorkOrderModel
import nl.pocketquest.server.api.crafting.WorkOrderQueue
import nl.pocketquest.server.api.user.UserCraftingCountRoute
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.utils.getLogger
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class WorkOrderFlowTest : WorkOrderBaseTest() {


    @Test
    fun shouldHaveCompleted() {
        db.clear()
        eventPool.clear()

        db.idGenerator = {
            when (it) {
                is WorkOrderModel -> "${it.recipe}_${it.count}"
                else -> it.toString()
            }
        }
        Server(kodein).init()
        runBlocking {
            user.inventory.item("cacao").give(3L)
            user.inventory.item("milk").give(2L)
            WorkOrderCreatedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CREATED, WorkOrderCreationData("chocolate_bear", "chocolate_bar", 1L),
                    1000L
            ))
            assertEquals(0L, user.inventory.item("cacao").count())
            assertEquals(0L, user.inventory.item("milk").count())
            dispatcher.runFully()
            WorkOrderClaimedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CLAIMED,
                    WorkOrderData("chocolate_bear", "chocolate_bar_1"),
                    1000L
            ))
            dispatcher.runFully()
            assertEquals(0L, user.inventory.item("cacao").count())
            assertEquals(0L, user.inventory.item("milk").count())
            assertEquals(2L, user.inventory.item("chocolate").count())
        }
    }

    @Test
    fun shouldHandleCancelling() {
        db.clear()
        eventPool.clear()

        db.idGenerator = {
            when (it) {
                is WorkOrderModel -> "${it.recipe}_${it.count}"
                else -> it.toString()
            }
        }
        Server(kodein).init()
        runBlocking {
            user.inventory.item("cacao").give(9L)
            user.inventory.item("milk").give(6L)
            WorkOrderCreatedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CREATED, WorkOrderCreationData("chocolate_bear", "chocolate_bar", 3L),
                    1000L
            ))
            assertEquals(0L, user.inventory.item("cacao").count())
            assertEquals(0L, user.inventory.item("milk").count())
            dispatcher.runUntilEventOfType(WorkorderStatus.WORK_ORDER_ONE_COMPLETED)
            dispatcher.executeNext()
            assertEquals(1L, db.get<Long>(UserCraftingCountRoute("chocolate_bear").route).content!!)
            WorkOrderCancelledEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CANCELLED,
                    WorkOrderData("chocolate_bear", "chocolate_bar_3"),
                    1000L
            ))
            dispatcher.runFully()
            assertNull(WorkOrderQueue.of("chocolate_bear", kodein).takeOldest())
            assertFalse(WorkOrder.byId("chocolate_bear", "chocolate_bar_3", kodein).exists())
            assertEquals(6L, user.inventory.item("cacao").count())
            assertEquals(4L, user.inventory.item("milk").count())
            assertEquals(2L, user.inventory.item("chocolate").count())
            assertEquals(0L, db.get<Long>(UserCraftingCountRoute("chocolate_bear").route).content!!)
        }
    }

    @Test
    fun shouldHaveCompletedMultiple() {
        db.clear()
        eventPool.clear()

        db.idGenerator = {
            when (it) {
                is WorkOrderModel -> "${it.recipe}_${it.count}"
                else -> it.toString()
            }
        }
        Server(kodein).init()
        runBlocking {
            user.inventory.item("cacao").give(9L)
            user.inventory.item("milk").give(6L)
            WorkOrderCreatedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CREATED, WorkOrderCreationData("chocolate_bear", "chocolate_bar", 2L),
                    1000L
            ))
            WorkOrderCreatedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CREATED, WorkOrderCreationData("chocolate_bear", "chocolate_bar", 1L),
                    1000L
            ))
            assertEquals(0L, user.inventory.item("cacao").count())
            assertEquals(0L, user.inventory.item("milk").count())
            dispatcher.runFully()
            WorkOrderClaimedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CLAIMED,
                    WorkOrderData("chocolate_bear", "chocolate_bar_2"),
                    1000L
            ))
            WorkOrderClaimedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CLAIMED,
                    WorkOrderData("chocolate_bear", "chocolate_bar_1"),
                    1000L
            ))
            dispatcher.runFully()
            assertEquals(0L, user.inventory.item("cacao").count())
            assertEquals(0L, user.inventory.item("milk").count())
            assertEquals(6L, user.inventory.item("chocolate").count())
        }
    }

}