package nl.pocketquest.pocketquest.views.main.recipe

import android.os.Bundle
import android.view.View
import nl.pocketquest.pocketquest.mvp.BaseFragment

class RecipeFragment : BaseFragment(), RecipeContract.RecipeView {

    private val presenter = RecipePresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onAttached()
    }

    override fun setFilter(filterDescription: String) {

    }

    override fun display(recipeModels: List<RecipeContract.RecipeModel>) {

    }

    override fun getView(): View? {
        return null
    }
}
