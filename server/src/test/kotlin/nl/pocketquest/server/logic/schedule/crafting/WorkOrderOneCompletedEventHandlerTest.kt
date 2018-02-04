package nl.pocketquest.server.logic.schedule.crafting

import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventPool
import org.junit.Assert.*
import org.junit.Test

class WorkOrderOneCompletedEventHandlerTest : WorkOrderBaseTest() {

    @Test
    fun shouldIncrementCompletedCountAndReschedule() {
        val (workOrderId, workOrderDataSource) = storeWorkOrder()
        val workOrderContent = workOrderDataSource.content!!
        workOrderContent.active = true
        workOrderContent.count = 3
        workOrderContent.completed = 0

        runBlocking {
            WorkOrderOneCompletedEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                            WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                            1000L
                    )
            )
            assertEquals(1L, workOrderContent.completed)
            assertTrue(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                                    WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                                    6000L
                            )
                    )
            )
        }
    }

    @Test
    fun shouldStopIfMaxReached() {
        val (workOrderId, workOrderDataSource) = storeWorkOrder()
        val workOrderContent = workOrderDataSource.content!!
        workOrderContent.active = true
        workOrderContent.count = 3
        workOrderContent.completed = 2

        runBlocking {
            WorkOrderOneCompletedEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                            WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                            1000L
                    )
            )
            assertEquals(3L, workOrderContent.completed)
            assertFalse(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                                    WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                                    6000L
                            )
                    )
            )
        }
    }

    @Test
    fun shouldStopIfDeleted() {
        val (workOrderId, workOrderDataSource) = storeWorkOrder()
        workOrderDataSource.content = null

        runBlocking {
            WorkOrderOneCompletedEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                            WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                            1000L
                    )
            )
            assertFalse(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                                    WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                                    6000L
                            )
                    )
            )
        }
    }

    @Test
    fun shouldStopIfNotActive() {
        val (workOrderId, workOrderDataSource) = storeWorkOrder()
        val workOrderContent = workOrderDataSource.content!!
        workOrderContent.active = false
        runBlocking {
            WorkOrderOneCompletedEventHandler(kodein).handle(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                            WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                            1000L
                    )
            )
            assertFalse(
                    eventPool.contains(
                            Event.of(
                                    WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                                    WorkOrderProcessData("chocolate_bear", workOrderId, 5000L),
                                    6000L
                            )
                    )
            )
        }
    }
}