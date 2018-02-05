package nl.pocketquest.server.api.user

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.dataaccesslayer.*

suspend fun updateUser(id: String, kodein: Kodein, update: suspend User.() -> Unit) {
    User.byId(id, kodein)
            .update()
}

class UserStatusRoute(id: String) : Findable<String> {
    override val route = listOf("users", id, "status")
    override val expectedType = String::class.java
}

class UserCraftingCountRoute(id: String) : Findable<Long> {
    override val route = listOf("users", id, "crafting_count")
    override val expectedType = Long::class.java
}

class UserMaxCraftingCountRoute(id: String) : Findable<Long> {
    override val route = listOf("users", id, "max_crafting_count")
    override val expectedType = Long::class.java
}

class InventoryRoute(id: String) : Findable<Map<String, Long>> {
    override val route = listOf("user_items", id, "backpack")
    override val expectedType: Class<Map<String, Long>> = Map::class.java as Class<Map<String, Long>>
}


class User internal constructor(
        private val statusRef: DataSource<String>,
        private val craftingCountRef: DataSource<Long>,
        private val maxCraftingCountRef: DataSource<Long>,
        val inventory: Inventory
) {

    suspend fun hasCraftingCountAvailable() = (craftingCountRef.readAsync() ?: 0L) < (maxCraftingCountRef.readAsync() ?: 1L)

    suspend fun incrementCraftingCount() = (maxCraftingCountRef.readAsync() ?: 1L)
            .let { maxCount ->
                craftingCountRef.transaction {
                    val current = it ?: 0L
                    if (current + 1 <= maxCount) {
                        TransactionResult.success(current + 1)
                    } else {
                        TransactionResult.abort()
                    }
                }
            }

    suspend fun decrementCraftingCount() = craftingCountRef.transaction {
        when {
            it == null -> TransactionResult.success(null) // We want to run the transaction again
            it - 1 >= 0 -> TransactionResult.success(it - 1)
            else -> TransactionResult.abort()
        }
    }

    suspend fun incrementMaxCraftingCount() = maxCraftingCountRef.transaction {
        TransactionResult.success((it ?: 1L) + 1)
    }

    suspend fun setStatus(newStatus: Status) = statusRef.transaction { currentValue ->
        if (currentValue == null) {
            return@transaction TransactionResult.success(newStatus.identifier)
        } else {
            return@transaction Status.fromExternalName(currentValue)
                    ?.takeIf { it.statusChangeValidator(newStatus) }
                    ?.let { TransactionResult.success(newStatus.identifier) }
                    ?: TransactionResult.abort()
        }
    }

    companion object {
        fun byId(id: String, kodein: Kodein) = User(
                kodein.instance<Database>()
                        .resolver
                        .resolve(UserStatusRoute(id)),
                kodein.instance<Database>()
                        .resolver
                        .resolve(UserCraftingCountRoute(id)),
                kodein.instance<Database>()
                        .resolver
                        .resolve(UserMaxCraftingCountRoute(id)),
                Inventory(InventoryRoute(id).route, kodein)
        )
    }
}
