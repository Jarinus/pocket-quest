package nl.pocketquest.server.entity

import nl.pocketquest.server.state.State

class ResourceNode(
        val id: String,
        private val family: String,
        val icon: String,
        val name: String,
        val tier: Int,
        val suppliedItems: Map<String, ResourceNodeSuppliedItem>
) {
    fun family() = State.resourceNodeFamily(family)

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
