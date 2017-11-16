package nl.pocketquest.server.entity

class Item(
        private val key: String,
        private val icon: String,
        private val name: String,
        private val tier: Int
) {
    fun key() = key

    fun icon() = icon

    fun name() = name

    fun tier() = tier
}

data class ItemModel(
        val icon: String = "",
        val name: String = "",
        val tier: Int = 0
) {
    fun toItem(key: String): Item {
        return Item(key, icon, name, tier)
    }
}
