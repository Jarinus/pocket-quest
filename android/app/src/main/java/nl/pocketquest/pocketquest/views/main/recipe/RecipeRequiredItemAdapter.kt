package nl.pocketquest.pocketquest.views.main.recipe

import android.graphics.Typeface
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.entities.FBItem
import nl.pocketquest.pocketquest.game.entities.load
import nl.pocketquest.pocketquest.views.main.recipe.RecipeModelAdapter.Companion.INNER_PADDING
import nl.pocketquest.pocketquest.views.main.recipe.RecipeModelAdapter.Companion.TITLE_ROW_FONT_SIZE
import nl.pocketquest.pocketquest.views.main.recipe.RecipeModelAdapter.Companion.TITLE_ROW_HEIGHT
import org.jetbrains.anko.*

class RecipeRequiredItemAdapter : BaseAdapter() {
    private var requiredItems: List<Pair<FBItem, Int>> = listOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: with(parent!!.context) {
            linearLayout {
                imageView {
                    id = R.id.recipeRequiredItemIconView

                    imageResource = R.drawable.ic_launcher_background
                }.lparams {
                    width = dip(TITLE_ROW_HEIGHT)
                    height = dip(TITLE_ROW_HEIGHT)

                    rightMargin = dip(INNER_PADDING)
                }

                textView {
                    id = R.id.recipeRequiredItemNameView

                    textSize = TITLE_ROW_FONT_SIZE
                    typeface = Typeface.DEFAULT_BOLD

                    gravity = CENTER_VERTICAL
                }.lparams {
                    height = dip(TITLE_ROW_HEIGHT)
                    weight = 1f
                }

                textView {
                    id = R.id.recipeRequiredItemCountView

                    textSize = TITLE_ROW_FONT_SIZE
                    typeface = Typeface.DEFAULT_BOLD

                    gravity = CENTER_VERTICAL
                }.lparams {
                    height = dip(TITLE_ROW_HEIGHT)

                    rightMargin = dip(INNER_PADDING)
                }

                lparams {
                    width = matchParent
                }
            }
        }

        val (requiredItem, requiredAmount) = getItem(position)
        val recipeRequiredIconView: ImageView = view.find(R.id.recipeRequiredItemIconView)
        val recipeRequiredItemNameView: TextView = view.find(R.id.recipeRequiredItemNameView)
        val recipeRequiredItemCountView: TextView = view.find(R.id.recipeRequiredItemCountView)

        recipeRequiredIconView.apply {
            load(context, requiredItem.icon)
        }
        recipeRequiredItemNameView.text = requiredItem.name
        recipeRequiredItemCountView.text = requiredAmount.toString()

        return view
    }

    override fun getItem(position: Int): Pair<FBItem, Int> = requiredItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = requiredItems.size

    fun newValues(requiredItems: Map<FBItem, Int>) {
        this.requiredItems = requiredItems.map { (item, amount) ->
            Pair(item, amount)
        }

        notifyDataSetChanged()
    }
}
