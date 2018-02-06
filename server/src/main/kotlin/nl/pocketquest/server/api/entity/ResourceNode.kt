package nl.pocketquest.server.api.entity

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.api.state.State

data class ResourceNode(
        val id: String,
        val family: String,
        val icon: String,
        val name: String,
        val tier: String,
        val iconEmpty: String,
        val suppliedItems: Map<String, ResourceNodeSuppliedItem>
) {
}

data class ResourceNodeModel(
        val family: String = "",
        val icon: String = "",
        val name: String = "",
        val tier: String = "",
        val icon_empty: String = "",
        var suppliedItems: Map<String, ResourceNodeSuppliedItem> = mapOf()
) {
    fun toResourceNode(id: String): ResourceNode {
        return ResourceNode(id, family, icon, name, tier, icon_empty, suppliedItems)
    }
}
