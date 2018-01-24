package nl.pocketquest.server.api.item

class Inventory internal constructor(private val route: List<String>) {

    fun item(name: String) = Item.byName(this, name)

    internal fun route() = route

    suspend fun transferTo(name: String, count: Long, destination: Inventory) {
        val itemsTaken = item(name).take(count)
        destination.item(name).give(itemsTaken)
    }
}