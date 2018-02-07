package nl.pocketquest.server.api.entity

data class CraftingRecipe(
        val recipeID: String,
        val requiredItems: Map<String, Long>,
        val acquiredItems: Map<String, Long>,
        val duration: Long
)

data class CraftingRecipeModel(
        val required_resources: Map<String, Long> = mapOf(),
        val acquired_resources: Map<String, Long> = mapOf(),
        val type: String = "",
        val duration: Long = 0L
) {
    fun toCraftingRecipe(key: String) = CraftingRecipe(
            key,
            required_resources,
            acquired_resources,
            duration
    )
}