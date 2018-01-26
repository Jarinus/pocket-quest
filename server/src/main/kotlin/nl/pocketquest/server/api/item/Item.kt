package nl.pocketquest.server.api.item

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.dataaccesslayer.*

data class ItemRoute(private val inventory: Inventory, private val name: String) : Findable<Long> {
    override val route = inventory.route() + name
    override val expectedType = Long::class.java
}

class Item internal constructor(private val reference: DataSource<Long>) {

    /**
     * Tries to take count items. If less than count items are present all items are taken
     */
    suspend fun take(count: Long): Long {
        var delta = 0L
        val success = reference.transaction {
            val current = it ?: 0L
            delta = minOf(current, count)
            TransactionResult.success(current - delta)
        }
        return if (success) delta else 0L
    }

    suspend fun give(count: Long) = reference.transaction {
        val current = it ?: 0L
        TransactionResult.success(current + count)
    }


    suspend fun count() = reference.readAsync() ?: 0L

    companion object {
        internal fun byName(inventory: Inventory, name: String, kodein: Kodein) = Item(
                kodein.instance<Database>().resolver.resolve(ItemRoute(inventory, name))
        )
    }
}