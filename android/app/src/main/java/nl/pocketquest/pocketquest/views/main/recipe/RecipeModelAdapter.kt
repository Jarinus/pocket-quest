package nl.pocketquest.pocketquest.views.main.recipe

import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.Typeface.DEFAULT_BOLD
import android.support.v7.widget.RecyclerView
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.entities.load
import nl.pocketquest.pocketquest.utils.fullHeightGridView
import nl.pocketquest.pocketquest.views.main.recipe.RecipeFragment.Companion.PADDING
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class RecipeModelAdapter : RecyclerView.Adapter<RecipeModelAdapter.ViewHolder>() {
    private var recipeModels: List<RecipeContract.RecipeModel> = listOf()
    private var openedRecipeContainer: GridView? = null
    private var openedRecipeExpansionIndicator: ImageView? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeNameView: TextView = view.find(R.id.recipeNameView)
        val recipeIconView: ImageView = view.find(R.id.recipeIconView)
        val recipeExpansionIndicator: ImageView = view.find(R.id.recipeExpansionIndicator)
        val recipeRequiredItemsContainer: GridView = view.find(R.id.recipeRequiredItemsContainer)
    }

    fun newValues(recipeModels: List<RecipeContract.RecipeModel>) {
        this.recipeModels = recipeModels

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            with(parent!!.context) {
                verticalLayout {
                    linearLayout {
                        backgroundResource = R.color.colorPrimary

                        imageView {
                            id = R.id.recipeIconView

                            imageResource = R.drawable.ic_launcher_background
                        }.lparams {
                            width = dip(TITLE_ROW_HEIGHT)
                            height = dip(TITLE_ROW_HEIGHT)

                            rightMargin = dip(PADDING)
                        }

                        textView {
                            id = R.id.recipeNameView

                            textSize = TITLE_ROW_FONT_SIZE
                            textColor = WHITE
                            typeface = DEFAULT_BOLD

                            gravity = CENTER_VERTICAL
                        }.lparams {
                            height = dip(TITLE_ROW_HEIGHT)
                            weight = 1f
                        }

                        imageView {
                            id = R.id.recipeExpansionIndicator

                            imageResource = R.drawable.ic_arrow_drop_down_white_36dp
                            isClickable = true
                        }.lparams {
                            height = dip(TITLE_ROW_HEIGHT)
                            width = dip(TITLE_ROW_HEIGHT)

                            leftMargin = dip(PADDING)
                        }

                        lparams {
                            width = matchParent

                            topMargin = dip(PADDING)
                            padding = dip(6)
                        }
                    }

                    fullHeightGridView {
                        id = R.id.recipeRequiredItemsContainer

                        backgroundColor = Color.LTGRAY
                        visibility = GONE

                        verticalSpacing = dip(REQUIRED_ITEMS_PADDING)
                        adapter = RecipeRequiredItemAdapter()

                        lparams {
                            padding = dip(REQUIRED_ITEMS_PADDING)
                        }
                    }.lparams {
                        leftMargin = dip(REQUIRED_ITEMS_PADDING)
                        rightMargin = dip(REQUIRED_ITEMS_PADDING)
                    }

                    lparams {
                        width = matchParent
                    }
                }
            }.let(::ViewHolder)

    override fun getItemCount(): Int = recipeModels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipeModel = recipeModels[position]
        val acquiredItem = recipeModel.acquiredItems.keys.first()

        holder.recipeNameView.text = acquiredItem.name
        holder.recipeIconView.apply {
            load(context, acquiredItem.icon)
        }
        holder.recipeExpansionIndicator.visibility = if (recipeModel.requiredItems.isEmpty()) GONE else VISIBLE
        holder.recipeExpansionIndicator.onClick {
            toggleVisibility(holder)
        }

        val adapter = holder.recipeRequiredItemsContainer.adapter as? RecipeRequiredItemAdapter
        adapter?.newValues(recipeModel.requiredItems)
    }

    private fun toggleVisibility(holder: ViewHolder) {
        openedRecipeContainer?.visibility = GONE
        openedRecipeExpansionIndicator?.imageResource = R.drawable.ic_arrow_drop_down_white_36dp

        when (openedRecipeContainer) {
            holder.recipeRequiredItemsContainer -> {
                openedRecipeContainer = null
                openedRecipeExpansionIndicator = null
            }
            else -> {
                openedRecipeContainer = holder.recipeRequiredItemsContainer
                        .also { it.visibility = VISIBLE }
                openedRecipeExpansionIndicator = holder.recipeExpansionIndicator
                        .also { it.imageResource = R.drawable.ic_arrow_drop_up_white_36dp }
            }
        }
    }

    companion object {
        const val TITLE_ROW_HEIGHT = 36
        const val TITLE_ROW_FONT_SIZE = 16f
        const val REQUIRED_ITEMS_PADDING = 6
    }
}
