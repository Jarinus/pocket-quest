package nl.pocketquest.pocketquest.game.crafting

data class Recipe(
        val id: String,
        val type: RecipeType,
        val duration: Long,
        val requiredItems: Map<String, Int>,
        val acquiredItems: Map<String, Int>
) {
    fun requiresItem(itemName: String) = requiredItems.containsKey(itemName)

    fun acquiresItem(itemName: String) = acquiredItems.containsKey(itemName)
}
