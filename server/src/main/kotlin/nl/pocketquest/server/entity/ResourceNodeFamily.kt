package nl.pocketquest.server.entity

class ResourceNodeFamily(
        private val id: String,
        private val gatheringToolTypes: Collection<String>,
        private val members: Collection<ResourceNode>
) {
    fun id() = id

    fun gatheringToolTypes() = gatheringToolTypes

    fun supportsToolType(toolType: String) = gatheringToolTypes.contains(toolType)

    fun members() = members

    fun isMember(resourceNode: ResourceNode) = members.contains(resourceNode)
}

data class ResourceNodeFamilyModel(
        val gathering_tool_types: Map<String, Boolean> = mapOf(),
        var members: Collection<ResourceNode> = listOf()
) {
    fun toResourceNodeFamily(id: String) = ResourceNodeFamily(id, gathering_tool_types.keys, members)
}
