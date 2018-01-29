package nl.pocketquest.pocketquest.views.main.recipe

import nl.pocketquest.pocketquest.game.crafting.Recipe
import nl.pocketquest.pocketquest.game.player.InventoryListener
import nl.pocketquest.pocketquest.game.player.Item
import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView

class RecipeContract {

    interface RecipeView : BaseView {
        fun applyFilter(predicate: (Recipe) -> Boolean)

        fun initialize(recipes: List<Recipe>)

        fun setLoading(loading: Boolean)

        fun updateInventoryState(items: List<Item>)
    }

    abstract class RecipePresenter(recipeView: RecipeView)
        : BasePresenter<RecipeView>(recipeView) {
        abstract fun onResetFilter()

        abstract fun onSelectRecipe(recipe: Recipe)

        abstract fun onSubmitFilter(predicate: (Recipe) -> Boolean)
    }
}
