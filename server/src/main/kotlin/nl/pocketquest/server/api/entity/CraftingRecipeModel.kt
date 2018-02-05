package nl.pocketquest.server.api.entity

data class CraftingRecipe(
        val recipeID: String,
        val requiredItems: Map<String, Long>,
        val acquiredItems: Map<String, Long>,
        val duration: Long
)

data class CraftingRecipeModel(
        val required_items: Map<String, Long> = mapOf(),
        val acquired_items: Map<String, Long> = mapOf(),
        val duration: Long = 0L
) {
    fun toCraftingRecipe(key: String) = CraftingRecipe(
            key,
            required_items,
            acquired_items,
            duration
    )
}