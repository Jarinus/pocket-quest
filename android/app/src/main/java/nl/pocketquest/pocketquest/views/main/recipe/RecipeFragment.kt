package nl.pocketquest.pocketquest.views.main.recipe

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.view.LayoutInflater
import android.view.ViewGroup
import nl.pocketquest.pocketquest.views.BaseFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI

class RecipeFragment : BaseFragment(), RecipeContract.RecipeView {
    private val presenter: RecipeContract.RecipePresenter = RecipePresenter(this)
    private val recipeModelAdapter: RecipeModelAdapter = RecipeModelAdapter(presenter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.onAttach()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onDetach()
    }

    override fun display(recipeModels: List<RecipeContract.RecipeModel>) =
            recipeModelAdapter.newValues(recipeModels)

    override fun setFilter(filterDescription: String) {
        info { "Filter: $filterDescription" }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = UI {
        recyclerView {
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = recipeModelAdapter

            clipToPadding = false

            lparams {
                width = matchParent
                height = matchParent

                leftPadding = dip(PADDING)
                rightPadding = dip(PADDING)
                bottomPadding = dip(PADDING)
            }
        }
    }.view

    companion object {
        const val PADDING = 12
    }
}
