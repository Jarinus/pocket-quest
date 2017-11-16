package nl.pocketquest.server.entity

class Item(
        val id: String,
        val icon: String,
        val name: String,
        val tier: Int
) {
    override fun toString(): String {
        return "Item(id='$id', icon='$icon', name='$name', tier=$tier)"
    }
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
