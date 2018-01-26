package nl.pocketquest.server.api.item

import com.github.salomonbrys.kodein.Kodein

class Inventory internal constructor(private val route: List<String>, private val kodein: Kodein) {

    fun item(name: String) = Item.byName(this, name, kodein)

    internal fun route() = route

    /**
     * Tries to transfer items between inventories. Returns how much items have been transferred
     */
    suspend fun transferTo(name: String, count: Long, destination: Inventory): Long {
        val itemsTaken = item(name).take(count)
        destination.item(name).give(itemsTaken)
        return itemsTaken
    }
}