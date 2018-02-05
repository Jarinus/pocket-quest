package nl.pocketquest.server.api.crafting

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.testhelpers.MockDataSource
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test
import org.hamcrest.collection.IsMapContaining
import org.hamcrest.collection.IsMapContaining.hasEntry

class WorkOrderQueueTest {

    val model = WorkOrderQueueModel()
    val data = MockDataSource(model, { it?.copy(queue = it.queue.toMutableMap()) })
    val queue = WorkOrderQueue(data)


    @Test
    fun shouldReturnOldest() {
        data.content!!.queue.clear()
        data.content!!.queue["first"] = 5
        data.content!!.queue["second"] = 6
        runBlocking {
            assertEquals("first", queue.takeOldest())
            assertEquals(1, data.content!!.queue.size)
            assertEquals("second", queue.takeOldest())
            assertEquals(null, queue.takeOldest())
        }
    }

    @Test
    fun shouldSubmit() {
        data.content!!.queue.clear()
        runBlocking {
            queue.submit("first", 1L)
            assertThat(data.content!!.queue, hasEntry("first", 1L))
        }
    }

    @Test
    fun shouldDelete() {
        data.content!!.queue.clear()
        runBlocking {
            assertFalse(queue.delete("first"))
            data.content!!.queue["first"] = 1
            assertTrue(queue.delete("first"))
            assertThat(data.content!!.queue, not((hasEntry("first", 1L))))
        }
    }
}