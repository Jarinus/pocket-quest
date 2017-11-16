package nl.pocketquest.server.entity

class ResourceNodeSuppliedItem(
        val itemId: String,
        val duration: Int,
        val amount: Pair<Int, Int>,
        val respawnTime: Int
) {
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
