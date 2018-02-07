package nl.pocketquest.server.api.user

import com.github.salomonbrys.kodein.Kodein
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.testhelpers.MockDataSource
import org.junit.Test

import org.junit.Assert.*

class UserTest {

    private val typeSource = MockDataSource(Status.IDLE.identifier)
    private val kodein = Kodein {}
    private val craftingCountSource = MockDataSource(0L)
    private val maxCraftingCountSource = MockDataSource(0L)
    private val user = User(typeSource,
            craftingCountSource,
            maxCraftingCountSource,
            Inventory(listOf("test", "user", "inventory"), kodein))

    @Test
    fun shouldNotIncrementCraftingCountBecauseMax() {
        maxCraftingCountSource.content = 1
        craftingCountSource.content = 1
        runBlocking {
            assertFalse(user.incrementCraftingCount())
            assertEquals(1L, craftingCountSource.content!!)
        }
    }

    @Test
    fun shouldIncrementCraftingCount() {
        maxCraftingCountSource.content = 1
        craftingCountSource.content = 0
        runBlocking {
            assertTrue(user.incrementCraftingCount())
            assertEquals(1L, craftingCountSource.content!!)
        }
    }

    @Test
    fun shouldHandleAbsentMaxCraftingCount() {
        maxCraftingCountSource.content = null
        craftingCountSource.content = 0
        runBlocking {
            assertTrue(user.incrementCraftingCount())
            assertEquals(1L, craftingCountSource.content!!)
            assertFalse(user.incrementCraftingCount())
            assertEquals(1L, craftingCountSource.content!!)
        }
    }

    @Test
    fun shouldHandleAbsentMaxCraftingCountAbsentNormalCraftingCount() {
        maxCraftingCountSource.content = null
        craftingCountSource.content = null
        runBlocking {
            assertTrue(user.incrementCraftingCount())
            assertEquals(1L, craftingCountSource.content!!)
            assertFalse(user.incrementCraftingCount())
            assertEquals(1L, craftingCountSource.content!!)
        }
    }

    @Test
    fun shouldDecrementCraftingCount() {
        craftingCountSource.content = 1L
        runBlocking {
            assertTrue(user.decrementCraftingCount())
            assertEquals(0L, craftingCountSource.content!!)
            assertFalse(user.decrementCraftingCount())
            assertEquals(0L, craftingCountSource.content!!)
        }
    }

    @Test
    fun shouldIncrementMaxCraftingCount() {
        maxCraftingCountSource.content = null
        runBlocking {
            assertTrue(user.incrementMaxCraftingCount())
            assertEquals(2L, maxCraftingCountSource.content!!)
            maxCraftingCountSource.content = 15
            assertTrue(user.incrementMaxCraftingCount())
            assertEquals(16L, maxCraftingCountSource.content!!)


        }
    }

    @Test
    fun shouldSetStatus() {
        runBlocking {
            assertTrue(user.setStatus(Status.GATHERING))
            assertEquals(Status.GATHERING.identifier, typeSource.content)
            assertFalse(user.setStatus(Status.GATHERING))
            assertEquals(Status.GATHERING.identifier, typeSource.content)
            assertTrue(user.setStatus(Status.IDLE))
            assertEquals(Status.IDLE.identifier, typeSource.content)
        }
    }
}
