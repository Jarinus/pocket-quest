package nl.pocketquest.server.entity

class ResourceNodeFamily(
        val id: String,
        val gatheringToolTypes: Collection<String>,
        val members: Collection<ResourceNode>
) {
    override fun toString(): String {
        return "ResourceNodeFamily(id='$id', gatheringToolTypes=$gatheringToolTypes, members=$members)"
    }
}

data class ResourceNodeFamilyModel(
        val gathering_tool_types: Map<String, Boolean> = mapOf(),
        var members: Collection<ResourceNode> = listOf()
) {
    fun toResourceNodeFamily(id: String) = ResourceNodeFamily(id, gathering_tool_types.keys, members)
}
