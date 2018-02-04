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

    suspend fun hasAll(items: Map<String, Long>): Boolean {
        return items.all { item(it.key).count() >= it.value }
    }

    /**
     * Returns true if exactly all items have been taken exactly count times
     * Else returns false and the inventory state will remain the same
     */
    suspend fun takeAllOrNothing(items: Map<String, Long>): Boolean {
        val resourcesFromUser = mutableMapOf<String, Long>()
        items.forEach {
            resourcesFromUser[it.key] = item(it.key).take(it.value)
        }
        if (items.all { resourcesFromUser[it.key] == it.value }) {
            return true
        }
        resourcesFromUser.forEach {
            item(it.key).give(it.value)
        }
        return false
    }
}