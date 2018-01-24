package nl.pocketquest.server.api.entity

class Item(
        val id: String,
        val icon: String,
        val name: String,
        val tier: String
) {
    override fun toString(): String {
        return "Item(id='$id', icon='$icon', name='$name', tier=$tier)"
    }
}

data class ItemModel(
        val icon: String = "",
        val name: String = "",
        val tier: String = ""
) {
    fun toItem(key: String): Item {
        return Item(key, icon, name, tier)
    }
}
