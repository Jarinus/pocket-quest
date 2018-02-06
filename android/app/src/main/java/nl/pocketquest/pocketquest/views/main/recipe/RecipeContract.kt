package nl.pocketquest.pocketquest.views.main.recipe

import nl.pocketquest.pocketquest.game.crafting.Recipe
import nl.pocketquest.pocketquest.views.BasePresenter
import nl.pocketquest.pocketquest.views.BaseView

class RecipeContract {

    interface RecipeView : BaseView {

        fun display(recipeModels: List<RecipeModel>)

        fun setFilter(filterDescription: String)
    }

    abstract class RecipePresenter(recipeView: RecipeView)
        : BasePresenter<RecipeView>(recipeView) {

        abstract fun onResetFilter()

        abstract fun onSelectRecipe(recipe: Recipe)

        abstract fun onSubmitFilter(predicate: (Recipe) -> Boolean)
    }

    /**
     * @param availabilityRange A range from the minimal and maximal possible number of crafted
     * items
     */
    data class RecipeModel(
            val recipe: Recipe,
            val availabilityRange: Pair<Int, Int>,
            val currentResources: Map<String, Int>
    )
}
