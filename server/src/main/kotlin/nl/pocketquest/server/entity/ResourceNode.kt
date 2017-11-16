package nl.pocketquest.server.entity

import nl.pocketquest.server.state.State

class ResourceNode(
        private val id: String,
        private val family: String,
        private val icon: String,
        private val name: String,
        private val tier: Int,
        private val suppliedItems: Map<String, ResourceNodeSuppliedItem>
) {
    fun id() = id

    fun family() = State.resourceNodeFamily(family)

    fun icon() = icon

    fun name() = name

    fun tier() = tier

    fun suppliedItems() = suppliedItems

    override fun toString(): String {
        return "ResourceNode(id='$id', family='$family', icon='$icon', name='$name', tier=$tier, suppliedItems=$suppliedItems)"
    }
}

data class ResourceNodeModel(
        val family: String = "",
        val icon: String = "",
        val name: String = "",
        val tier: Int = 0,
        var suppliedItems: Map<String, ResourceNodeSuppliedItem> = mapOf()
) {
    fun toResourceNode(id: String): ResourceNode {
        return ResourceNode(id, family, icon, name, tier, suppliedItems)
    }
}
