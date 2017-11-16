package nl.pocketquest.server.entity

class ResourceNodeSuppliedItem(
        private val itemId: String,
        private val duration: Int,
        private val amount: Pair<Int, Int>,
        private val respawnTime: Int
) {
    fun itemId() = itemId

    fun duration() = duration

    fun amount() = amount

    fun respawnTime() = respawnTime

    override fun toString(): String {
        return "ResourceNodeSuppliedItem(itemId='$itemId', duration=$duration, amount=$amount, respawnTime=$respawnTime)"
    }
}

data class ResourceNodeSuppliedItemModel(
        val duration: Int = 0,
        val max_amount: Int = 0,
        val min_amount: Int = 0,
        val respawn_time: Int = 0
) {
    fun toSuppliedItem(itemId: String): ResourceNodeSuppliedItem {
        return ResourceNodeSuppliedItem(itemId, duration, min_amount to max_amount, respawn_time)
    }
}
