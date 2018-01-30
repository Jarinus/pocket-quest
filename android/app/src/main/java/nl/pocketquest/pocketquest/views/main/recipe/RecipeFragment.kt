package nl.pocketquest.pocketquest.views.main.recipe

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.views.BaseFragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class RecipeFragment : BaseFragment(), RecipeContract.RecipeView {

    private lateinit var presenter: RecipePresenter
    private var filterDescription: String = ""
    private val recipeAdapter = RecipeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = RecipePresenter(this)
                .also(RecipePresenter::onAttached)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = UI {
        recyclerView {
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = recipeAdapter

            lparams {
                width = matchParent
                height = matchParent
            }
        }
    }.view

    override fun setFilter(filterDescription: String) {
        this.filterDescription = filterDescription
    }

    override fun display(recipeModels: List<RecipeContract.RecipeModel>) {
        recipeAdapter.swap(recipeModels)
    }

    class RecipeAdapter(
            private var recipeModels: List<RecipeContract.RecipeModel> = listOf()
    ) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), AnkoLogger {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleView: TextView = view.findViewById(R.id.recipeTitleView)
        }

        fun swap(recipeModels: List<RecipeContract.RecipeModel>) {
            this.recipeModels = recipeModels

            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            return with(parent!!.context) {
                verticalLayout {
                    lparams {
                        width = matchParent
                    }

                    textView {
                        id = R.id.recipeTitleView
                    }
                }.let(::ViewHolder)
            }
        }

        override fun getItemCount(): Int = recipeModels.count()

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val recipeModel = recipeModels[position]

            holder?.titleView?.text = recipeModel.recipe.id
        }
    }

}
