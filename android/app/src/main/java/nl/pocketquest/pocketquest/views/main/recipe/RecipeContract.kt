package nl.pocketquest.pocketquest.views.main.recipe

import nl.pocketquest.pocketquest.game.crafting.Recipe
import nl.pocketquest.pocketquest.game.crafting.RecipeType
import nl.pocketquest.pocketquest.game.entities.FBItem
import nl.pocketquest.pocketquest.views.BasePresenter
import nl.pocketquest.pocketquest.views.BaseView

class RecipeContract {

    interface RecipeView : BaseView {

        fun display(recipeModels: List<RecipeModel>)

        fun setFilter(filterDescription: String)
    }

    abstract class RecipePresenter(recipeView: RecipeView) : BasePresenter<RecipeView>(recipeView) {

        abstract fun onResetFilter()

        abstract fun onSelectRecipe(recipeId: String, count: Int)

        abstract fun onSubmitFilter(predicate: (Recipe) -> Boolean)
    }

    /**
     * @param availabilityRange A range from the minimal and maximal possible number of crafted
     * items
     */
    data class RecipeModel(
            val id: String,
            val requiredItems: Map<FBItem, Int>,
            val acquiredItems: Map<FBItem, Int>,
            val type: RecipeType,
            val duration: Long,
            val availabilityRange: Pair<Int, Int>,
            val currentResources: Map<FBItem, Int>
    )
}
