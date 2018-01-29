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
                Inventory(InventoryRoute(id).route, kodein)
        )
    }
}