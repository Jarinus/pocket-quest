package nl.pocketquest.pocketquest.views.main.recipe

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import nl.pocketquest.pocketquest.R
import org.jetbrains.anko.find
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class RecipeModelAdapter : RecyclerView.Adapter<RecipeModelAdapter.ViewHolder>() {
    private var recipeModels: List<RecipeContract.RecipeModel> = listOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeNameView: TextView = view.find(R.id.recipeNameView)
    }

    fun newValues(recipeModels: List<RecipeContract.RecipeModel>) {
        this.recipeModels = recipeModels

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            with(parent!!.context) {
                verticalLayout {
                    textView {
                        id = R.id.recipeNameView
                    }
                }
            }.let(::ViewHolder)

    override fun getItemCount(): Int = recipeModels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipeModel = recipeModels[position]

        holder.recipeNameView.text = recipeModel.id
    }
}
