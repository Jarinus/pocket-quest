package nl.pocketquest.pocketquest.views.main.recipe

import nl.pocketquest.pocketquest.mvp.BaseFragment

class RecipeFragment : BaseFragment(), RecipeContract.RecipeView {

    private val presenter = RecipePresenter(this)

    override fun setFilter(filterDescription: String) {

    }

    override fun display(recipeModels: List<RecipeContract.RecipeModel>) {

    }
}
