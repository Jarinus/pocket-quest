package nl.pocketquest.server.api.user

import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.DatabaseConfiguration
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.dataaccesslayer.TransactionResult
import nl.pocketquest.server.api.item.Inventory

suspend fun updateUser(id: String, update: suspend User.() -> Unit) {
    User.byId(id).update()
}

class StatusRoute(id: String) : Findable<String> {
    override val route = listOf("users", id, "status")
    override val expectedType = String::class.java
}

class InventoryRoute(id: String) : Findable<Map<String, Long>> {
    override val route = listOf("user_items", id, "backpack")
    override val expectedType: Class<Map<String, Long>> = Map::class.java as Class<Map<String, Long>>
}


class User internal constructor(
        private val statusRef: DataSource<String>,
        val inventory: Inventory
) {


    suspend fun setStatus(newStatus: Status) = statusRef.transaction { currentValue ->
        if (currentValue == null) {
            return@transaction TransactionResult.success(newStatus.externalName)
        } else {
            return@transaction Status.fromExternalName(currentValue)
                    ?.takeIf { it.statusChangeValidator(newStatus) }
                    ?.let { TransactionResult.success(newStatus.externalName) }
                    ?: TransactionResult.abort()
        }
    }

    companion object {
        fun byId(id: String) = User(
                DatabaseConfiguration.database.resolver.resolve(StatusRoute(id)),
                Inventory(InventoryRoute(id).route)
        )
    }
}