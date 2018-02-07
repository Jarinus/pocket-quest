package nl.pocketquest.server.api.crafting

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.testhelpers.MockDataSource
import org.junit.Assert.*
import org.junit.Test

class WorkOrderTest {

    private val data = MockDataSource<WorkOrderModel>(WorkOrderModel(
            3, 0, "chocolate_bar", false, 0
    )) {
        it?.copy()
    }

    @Test
    fun shouldReadRecipe() {
        runBlocking {
            assertEquals("chocolate_bar", WorkOrder(data).recipe())
        }
    }

    @Test
    fun shouldIncrementWhileActive() {
        val workOrder = WorkOrder(data)
        data.content!!.active = true
        runBlocking {
            assertTrue(workOrder.addOneCompleted())
            assertEquals(1L, data.content!!.completed)
            assertTrue(workOrder.addOneCompleted())
            assertEquals(2L, data.content!!.completed)
            assertTrue(workOrder.addOneCompleted())
            assertEquals(3L, data.content!!.completed)
            assertFalse(workOrder.addOneCompleted())
            assertEquals(3L, data.content!!.completed)
            assertFalse(workOrder.addOneCompleted())
            assertEquals(3L, data.content!!.completed)
        }
    }

    @Test
    fun shouldNotIncrementWhileNotActive() {
        val workOrder = WorkOrder(data)
        data.content!!.active = false
        runBlocking {
            assertFalse(workOrder.addOneCompleted())
            assertEquals(0L, data.content!!.completed)
        }
    }

    @Test
    fun shouldSetActive() {
        val workOrder = WorkOrder(data)
        data.content!!.active = false
        runBlocking {
            workOrder.updateObject(active = true)
            assertEquals(true, data.content!!.active)
            workOrder.updateObject(active = false)
            assertEquals(false, data.content!!.active)
        }
    }

    @Test
    fun shouldBeCompleted() {
        val workOrder = WorkOrder(data)
        data.content!!.completed = 5
        data.content!!.count = 5
        runBlocking {
            assertTrue(workOrder.finished())
            data.content!!.completed = 4
            assertFalse(workOrder.finished())
        }
    }
}