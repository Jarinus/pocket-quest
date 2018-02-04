package nl.pocketquest.server.api.item

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.testhelpers.TestDB
import org.junit.Assert.*
import org.junit.Test

class InventoryTest {

    private val db = TestDB(mapOf())
    private val kodein = Kodein {
        bind<Database>() with singleton { db }
    }
    private val inventory = Inventory(listOf("users", "chocolate_bear"), kodein)

    @Test
    fun shouldHaveItems() {
        db.clear()
        val items = mapOf(
                "stone" to 6L,
                "wood" to 6L
        )
        giveItems(items)
        runBlocking {
            assertTrue(inventory.hasAll(items))
        }
    }

    @Test
    fun shouldNotHaveItemsWhenEmpty() {
        db.clear()
        runBlocking {
            assertFalse(inventory.hasAll(mapOf(
                    "stone" to 6L,
                    "wood" to 6L
            )))
        }
    }

    @Test
    fun shouldNotHaveItemsWhenTooLittle() {
        db.clear()
        val items = mapOf(
                "stone" to 5L,
                "wood" to 6L
        )
        giveItems(items)
        runBlocking {
            assertFalse(inventory.hasAll(mapOf(
                    "stone" to 6L,
                    "wood" to 6L
            )))
        }
    }

    @Test
    fun shouldTakeItems() {
        db.clear()
        val items = mapOf(
                "stone" to 8L,
                "wood" to 6L
        )
        giveItems(items)
        runBlocking {
            assertTrue(inventory.takeAllOrNothing(mapOf(
                    "stone" to 6L,
                    "wood" to 5L
            )))
            assertEquals(2L, inventory.item("stone").count())
            assertEquals(1L, inventory.item("wood").count())
        }
    }

    @Test
    fun shouldNotTakeItemsWhenTooLittle() {
        db.clear()
        val items = mapOf(
                "stone" to 8L,
                "wood" to 6L
        )
        giveItems(items)
        runBlocking {
            assertFalse(inventory.takeAllOrNothing(mapOf(
                    "stone" to 9L,
                    "wood" to 5L
            )))
            assertEquals(8L, inventory.item("stone").count())
            assertEquals(6L, inventory.item("wood").count())
        }
    }


    private fun giveItems(items: Map<String, Long>) {
        runBlocking {
            items.forEach {
                inventory.item(it.key).give(it.value)
            }
        }
    }
}