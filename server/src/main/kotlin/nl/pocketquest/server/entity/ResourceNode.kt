package nl.pocketquest.server.entity

import nl.pocketquest.server.state.State

class ResourceNode(
        private val id: String,
        private val family: String,
        private val icon: String,
        private val name: String,
        private val tier: Int
) {
    fun id() = id

    fun family() = State.resourceNodeFamily(family)

    fun icon() = icon

    fun name() = name

    fun tier() = tier
}

data class ResourceNodeModel(
        val family: String = "",
        val icon: String = "",
        val name: String = "",
        val tier: Int = 0
) {
    fun toResourceNode(id: String): ResourceNode {
        return ResourceNode(id, family, icon, name, tier)
    }
}
