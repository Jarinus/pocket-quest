package nl.pocketquest.server.api.entity

data class GatheringToolFamily(
        val id: String,
        val members: Collection<String>
) {
    override fun toString(): String {
        return "GatheringToolFamily(id='$id', members=$members)"
    }
}

data class GatheringToolFamilyModel(

        val members: Map<String, Boolean> = mapOf()
) {
    fun toGatheringToolFamily(id: String) = GatheringToolFamily(id, members.keys)
}
