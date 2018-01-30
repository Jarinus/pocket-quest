package nl.pocketquest.pocketquest.views.main.recipe

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.crafting.Recipe
import nl.pocketquest.pocketquest.game.entities.Entities
import nl.pocketquest.pocketquest.game.player.CompleteInventoryListener
import nl.pocketquest.pocketquest.game.player.InventoryListener
import nl.pocketquest.pocketquest.game.player.InventoryMap
import kotlin.properties.Delegates.observable

typealias Predicate<T> = (T) -> Boolean

class RecipePresenter(
        val recipeView: RecipeContract.RecipeView,
        val inventoryMap: InventoryMap
) : RecipeContract.RecipePresenter(recipeView),
        InventoryListener by inventoryMap,
        CompleteInventoryListener {
    init {
        inventoryMap.observer = this
    }

    private var recipes by observable(mapOf<String, Recipe>()) { _, _, new -> updateView() }
    private var inventory by observable(mapOf<String, Long>()) { _, _, new -> updateView() }
    companion object {
        private val returnTrue: Predicate<RecipeContract.RecipeModel> = {true}
    }
    private var predicate by observable(returnTrue) { _, _, new -> updateView() }

    override/*@InventoryListener*/ fun onUpdate(map: Map<String, Long>) {
        inventory = map
    }


    private data class IngredientAmounts(
            val ingredient: String,
            val having: Int,
            val required: Int
    ) {
        fun times() = having / required
    }

    private fun Recipe.withIngredientAmounts(having: Map<String, Long>) = this to acquiredItem.map { (item, count) ->
        IngredientAmounts(
                ingredient = item,
                required = count,
                having = having[item]?.toInt() ?: 0
        )
    }

    private fun createRecipeModel(recipe: Recipe, ingredientAmounts: Collection<IngredientAmounts>) = RecipeContract.RecipeModel(
            recipe = recipe,
            availabilityRange = 0 to (ingredientAmounts.map(IngredientAmounts::times).min() ?: 0),
            currentResources = ingredientAmounts.map { it.ingredient to it.having }.toMap()
    )

    private fun updateView() {
        recipes.map { (_, recipe) -> recipe.withIngredientAmounts(inventory) }
                .map { (recipe, ingredientAmounts) -> createRecipeModel(recipe, ingredientAmounts) }
                .filter(predicate)
                .also { recipeView.display(it) }
    }

    override fun onAttached() {
        async(CommonPool) {
            recipes = Entities.getRecipes()
        }
    }

    override fun onResetFilter() {
        predicate = returnTrue
    }

    override fun onSelectRecipe(recipe: Recipe) {
        
    }

    override fun onSubmitFilter(predicate: (Recipe) -> Boolean) {

    }
}
