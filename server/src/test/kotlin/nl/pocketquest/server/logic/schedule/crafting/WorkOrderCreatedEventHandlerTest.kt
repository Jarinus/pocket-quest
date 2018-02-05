package nl.pocketquest.server.logic.schedule.crafting

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.crafting.WorkOrder
import nl.pocketquest.server.api.crafting.WorkOrderModel
import nl.pocketquest.server.api.crafting.WorkOrderQueue
import nl.pocketquest.server.api.crafting.WorkOrderRoute
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.utils.getLogger
import org.junit.Assert.*
import org.junit.Test

class WorkOrderCreatedEventHandlerTest : WorkOrderBaseTest() {


    @Test
    fun shouldNotCreateWorkOrder() {
        db.clear()
        eventPool.clear()
        runBlocking {
            user.inventory.item("cacao").give(2L)
            user.inventory.item("milk").give(2L)
            WorkOrderCreatedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CREATED, WorkOrderCreationData("chocolate_bear", "chocolate_bar", 1L),
                    1000L
            ))
            assertEquals(2L, user.inventory.item("cacao").count())
            assertEquals(2L, user.inventory.item("milk").count())
            assertTrue(eventPool.empty())
        }
    }

    @Test
    fun shouldNotCreateWorkOrderWithMultiple() {
        db.clear()
        eventPool.clear()
        runBlocking {
            user.inventory.item("cacao").give(3L)
            user.inventory.item("milk").give(2L)
            WorkOrderCreatedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CREATED, WorkOrderCreationData("chocolate_bear", "chocolate_bar", 2L),
                    1000L
            ))
            assertEquals(3L, user.inventory.item("cacao").count())
            assertEquals(2L, user.inventory.item("milk").count())
            assertTrue(eventPool.empty())
        }
    }

    @Test
    fun shouldCreateWorkOrder() {
        db.clear()
        eventPool.clear()
        val queue = WorkOrderQueue.of("chocolate_bear", kodein)
        runBlocking {
            user.inventory.item("cacao").give(3L)
            user.inventory.item("milk").give(2L)
            WorkOrderCreatedEventHandler(kodein).handle(Event.of(
                    WorkorderStatus.WORK_ORDER_CREATED, WorkOrderCreationData("chocolate_bear", "chocolate_bar", 1L),
                    1000L
            ))
            assertEquals(0L, user.inventory.item("cacao").count())
            assertEquals(0L, user.inventory.item("milk").count())
            val workOrder = queue.takeOldest()
            assertNotNull(workOrder)
            val model = db.get<WorkOrderModel>(WorkOrderRoute("chocolate_bear", workOrder!!).route)
            assertEquals(1L, model.content!!.count)
            assertEquals(1000L, model.content!!.submitted_at)
            assertEquals(0L, model.content!!.completed)
            assertEquals(false, model.content!!.active)
            assertEquals("chocolate_bar", model.content!!.recipe)
            assertTrue(eventPool.contains(
                    Event.of(WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED, WorkOrderUserData(
                            "chocolate_bear"
                    ), 1000L)
            ))
        }
    }
}