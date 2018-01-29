package nl.pocketquest.server.api.item

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.testhelpers.FailingTransActionDataSource
import nl.pocketquest.server.testhelpers.MockDataSource

import org.junit.Assert.*
import org.junit.Test

class ItemTest {

    @Test
    fun shouldTakeItemsUntilEmpty() {
        val source = MockDataSource<Long>(75)
        val item = Item(source)
        runBlocking {
            val itemsTaken = item.take(14)
            assertEquals(14, itemsTaken)
            val shouldBe61 = item.take(2000)
            assertEquals(61, shouldBe61)
            val zero = item.take(200)
            assertEquals(0, zero)
        }
    }

    @Test
    fun shouldHandleFailingTransaction() {
        val source = FailingTransActionDataSource(44L)
        val item = Item(source)
        runBlocking {
            val taken = item.take(3)
            assertEquals(0, taken)
        }
    }
}
