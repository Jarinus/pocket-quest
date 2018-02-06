package nl.pocketquest.pocketquest.views.main.recipe

import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.Typeface.DEFAULT_BOLD
import android.support.v7.widget.RecyclerView
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.entities.load
import nl.pocketquest.pocketquest.utils.fullHeightGridView
import nl.pocketquest.pocketquest.utils.onProgressChange
import nl.pocketquest.pocketquest.views.main.recipe.RecipeFragment.Companion.PADDING
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class RecipeModelAdapter(val presenter: RecipeContract.RecipePresenter) : RecyclerView.Adapter<RecipeModelAdapter.ViewHolder>() {
    private var recipeModels: List<RecipeContract.RecipeModel> = listOf()
    private var openedRecipeContainer: LinearLayout? = null
    private var openedRecipeExpansionIndicator: ImageView? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeTitleContainer: LinearLayout = view.find(R.id.recipeTitleContainer)
        val recipeNameView: TextView = view.find(R.id.recipeNameView)
        val recipeIconView: ImageView = view.find(R.id.recipeIconView)
        val recipeExpansionIndicator: ImageView = view.find(R.id.recipeExpansionIndicator)

        val recipeExpansionContainer: LinearLayout = view.find(R.id.recipeExpansionContainer)
        val recipeRequiredItemsContainer: GridView = view.find(R.id.recipeRequiredItemsContainer)
        val recipeSelectAmountBar: SeekBar = view.find(R.id.recipeSelectAmountBar)
        val recipeCraftButton: TextView = view.find(R.id.recipeCraftButton)
    }

    fun newValues(recipeModels: List<RecipeContract.RecipeModel>) {
        this.recipeModels = recipeModels

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            with(parent!!.context) {
                verticalLayout {
                    id = R.id.recipeTitleContainer

                    isClickable = true

                    linearLayout {
                        backgroundResource = R.color.colorPrimary

                        imageView {
                            id = R.id.recipeIconView

                            imageResource = R.drawable.ic_launcher_background
                        }.lparams {
                            height = dip(TITLE_ROW_HEIGHT)
                            width = dip(TITLE_ROW_HEIGHT)

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
                        }.lparams {
                            height = dip(TITLE_ROW_HEIGHT)
                            width = dip(TITLE_ROW_HEIGHT)

                            leftMargin = dip(PADDING)
                        }

                        lparams {
                            width = matchParent

                            topMargin = dip(PADDING)
                            padding = dip(INTERNAL_PADDING)
                        }
                    }

                    verticalLayout {
                        id = R.id.recipeExpansionContainer
                        visibility = GONE

                        fullHeightGridView {
                            id = R.id.recipeRequiredItemsContainer

                            backgroundColor = Color.LTGRAY

                            verticalSpacing = dip(INTERNAL_PADDING)
                            adapter = RecipeRequiredItemAdapter()

                            lparams {
                                padding = dip(INTERNAL_PADDING)
                            }
                        }.lparams {
                            leftMargin = dip(INTERNAL_PADDING)
                            rightMargin = dip(INTERNAL_PADDING)
                        }

                        linearLayout {
                            backgroundResource = R.color.colorPrimary

                            seekBar {
                                id = R.id.recipeSelectAmountBar

                                max = 1
                            }.lparams {
                                height = dip(TITLE_ROW_HEIGHT)
                                weight = 1f
                            }

                            textView {
                                id = R.id.recipeCraftButton

                                textColor = WHITE
                                typeface = DEFAULT_BOLD
                                gravity = CENTER
                            }.lparams {
                                height = dip(TITLE_ROW_HEIGHT)

                                leftMargin = dip(INTERNAL_PADDING)
                                rightMargin = dip(INTERNAL_PADDING)
                            }

                            lparams {
                                width = matchParent

                                padding = dip(INTERNAL_PADDING)
                            }
                        }
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

        holder.recipeTitleContainer.onClick {
            toggleVisibility(holder)
        }

        val availableToCraft = recipeModel.availabilityRange.second
        when (availableToCraft) {
            1 -> {
                holder.recipeSelectAmountBar.max = 1
                holder.recipeSelectAmountBar.progress = 1
                holder.recipeSelectAmountBar.isEnabled = false
            }
            else ->
                holder.recipeSelectAmountBar.max = availableToCraft - 1
        }

        holder.recipeCraftButton.text = craftButtonText(1, availableToCraft)

        holder.recipeCraftButton.onClick {
            val amount = holder.recipeSelectAmountBar.progress + 1

            if (amount > availableToCraft) {
                error { "Invalid amount to craft: $amount > $availableToCraft" }
            }

            presenter.onSelectRecipe(recipeModel.id, amount)
        }

        holder.recipeSelectAmountBar.onProgressChange {
            holder.recipeCraftButton.text = craftButtonText(it + 1, availableToCraft)
        }

        val adapter = holder.recipeRequiredItemsContainer.adapter as? RecipeRequiredItemAdapter
        adapter?.newValues(recipeModel.requiredItems)
    }

    private fun craftButtonText(progress:Int, max:Int): String = "CRAFT $progress/$max"

    private fun toggleVisibility(holder: ViewHolder) {
        openedRecipeContainer?.visibility = GONE
        openedRecipeExpansionIndicator?.imageResource = R.drawable.ic_arrow_drop_down_white_36dp

        when (openedRecipeContainer) {
            holder.recipeExpansionContainer -> {
                openedRecipeContainer = null
                openedRecipeExpansionIndicator = null
            }
            else -> {
                openedRecipeContainer = holder.recipeExpansionContainer
                        .also { it.visibility = VISIBLE }
                openedRecipeExpansionIndicator = holder.recipeExpansionIndicator
                        .also { it.imageResource = R.drawable.ic_arrow_drop_up_white_36dp }
            }
        }
    }

    companion object {
        const val TITLE_ROW_HEIGHT = 36
        const val TITLE_ROW_FONT_SIZE = 16f
        const val INTERNAL_PADDING = 6
    }
}
