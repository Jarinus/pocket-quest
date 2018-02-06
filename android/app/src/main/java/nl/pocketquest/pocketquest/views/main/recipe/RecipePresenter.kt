package nl.pocketquest.pocketquest.views.main.recipe

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.crafting.Recipe
import nl.pocketquest.pocketquest.game.entities.Entities
import nl.pocketquest.pocketquest.game.entities.FBItem
import nl.pocketquest.pocketquest.game.player.CompleteInventoryListener
import nl.pocketquest.pocketquest.game.player.Inventory
import nl.pocketquest.pocketquest.game.player.InventoryListener
import nl.pocketquest.pocketquest.game.player.InventoryMap
import nl.pocketquest.pocketquest.utils.mapKeysNotNull
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.info
import kotlin.properties.Delegates.observable

typealias Predicate<T> = (T) -> Boolean

class RecipePresenter(
        val recipeView: RecipeContract.RecipeView,
        val inventoryMap: InventoryMap = InventoryMap()
) : RecipeContract.RecipePresenter(recipeView),
        InventoryListener by inventoryMap,
        CompleteInventoryListener {
    companion object {
        private val returnTrue: Predicate<Recipe> = { true }
    }

    private var predicate by observable(returnTrue) { _, _, _ -> updateView() }
    private var recipes by observable(mapOf<String, Recipe>()) { _, _, _ -> updateView() }
    private var inventory by observable(mapOf<String, Long>()) { _, _, _ -> updateView() }
    private lateinit var userInventory: Inventory

    init {
        inventoryMap.observer = this
        whenLoggedIn {
            userInventory = Inventory.getUserInventory(it.uid)
            userInventory.addInventoryListener(this)
        }
    }

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

    private fun Recipe.withIngredientAmounts(having: Map<String, Long>) =
            this to requiredItems.map { (item, count) ->
                IngredientAmounts(
                        ingredient = item,
                        required = count,
                        having = having[item]?.toInt() ?: 0
                )
            }

    private suspend fun createRecipeModel(recipe: Recipe, ingredientAmounts: Collection<IngredientAmounts>): RecipeContract.RecipeModel {
        val items: Map<String, FBItem> = Entities.getItems()
        return RecipeContract.RecipeModel(
                id = recipe.id,
                availabilityRange = 0 to (ingredientAmounts.map(IngredientAmounts::times).min()
                        ?: 0),
                type = recipe.type,
                duration = recipe.duration,
                currentResources = ingredientAmounts.mapNotNull {
                    val item = items[it.ingredient]
                    when (item) {
                        null -> null
                        else -> item to it.having
                    }
                }.toMap(),
                requiredItems = recipe.requiredItems.mapKeysNotNull { items[it.key] },

                acquiredItems = recipe.acquiredItems.mapKeysNotNull { items[it.key] }
        )
    }

    private fun updateView() {
        info { "Updating view with recipes with $recipes" }
        info { "Updating view with recipes current inventory = $inventory" }
        async(CommonPool) {
            recipes.filterValues(predicate)
                    .map { (_, recipe) -> recipe.withIngredientAmounts(inventory) }
                    .also { info { "Recipes with ingredientAmounts $it" } }
                    .map { (recipe, ingredientAmounts) -> createRecipeModel(recipe, ingredientAmounts) }
                    .also { info { "Recipe models $it" } }
                    .filter { it.availabilityRange.second != 0 }
                    .also { info { "Recipe models filtered $it" } }
                    .also {
                        async(UI) {
                            recipeView.display(
                                    it.sortedWith(compareBy(
                                            { it.acquiredItems.keys.first().tier },
                                            { it.type.ordinal },
                                            { it.acquiredItems.keys.first().name }
                                    )))
                        }
                    }
        }
    }

    override fun onAttach() {
        async(CommonPool) {
            info { "Loading recipes " }
            recipes = Entities.getRecipes()
            info { "Loaded recipes " }
        }
    }

    override fun onDetach() {
        userInventory.removeInventoryListener(this)
    }

    override fun onResetFilter() {
        predicate = returnTrue
    }

    override fun onSelectRecipe(recipeId: String, count: Int) {
    }

    override fun onSubmitFilter(predicate: (Recipe) -> Boolean) {
        this.predicate = predicate
    }
}
