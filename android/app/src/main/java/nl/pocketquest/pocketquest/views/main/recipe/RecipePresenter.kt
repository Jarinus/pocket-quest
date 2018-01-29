package nl.pocketquest.pocketquest.views.main.recipe

import nl.pocketquest.pocketquest.game.crafting.Recipe
import nl.pocketquest.pocketquest.game.player.InventoryListener
import nl.pocketquest.pocketquest.game.player.Item

class RecipePresenter(recipeView: RecipeContract.RecipeView)
    : RecipeContract.RecipePresenter(recipeView),
        InventoryListener {

    override fun newInventoryState(item: Item) {

    }

    override fun itemAdded(item: Item, prevCount: Long) {

    }

    override fun itemRemoved(item: Item) {

    }

    override fun onResetFilter() {

    }

    override fun onSelectRecipe(recipe: Recipe) {

    }

    override fun onSubmitFilter(predicate: (Recipe) -> Boolean) {

    }
}
