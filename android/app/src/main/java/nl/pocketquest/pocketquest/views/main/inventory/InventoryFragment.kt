package nl.pocketquest.pocketquest.views.main.inventory

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.entities.load
import nl.pocketquest.pocketquest.game.player.Item
import nl.pocketquest.pocketquest.mvp.BaseFragment
import nl.pocketquest.pocketquest.utils.squaredRelativeLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.fitWindowsFrameLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.ctx

data class InventoryItem(val id: String, val name: String, val count: Long, val img: String)

private fun Item.toInventoryItem(lambda: (InventoryItem) -> Unit) {
    async(UI) {
        try {
            val (name, icon, _) = getItemProperties()!!
            val item = InventoryItem(itemName, name, itemCount, icon)
            lambda(item)
        } catch (e: Exception) {
            Log.wtf("inventory", e.getStackTraceString())
        }
    }
}

class InventoryFragment : BaseFragment(), InventoryContract.InventoryView {
    private val presenter = InventoryPresenter(this)
    private lateinit var mAdapter: InvertoryItemAdapter
    override fun addItem(item: Item) = item.toInventoryItem {
        mAdapter.add(it)
    }

    override fun removeItem(item: Item) = item.toInventoryItem {
        mAdapter.remove(it)
    }

    override fun onInventoryState(item: Item) = item.toInventoryItem {
        mAdapter.update(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attached()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAdapter = InvertoryItemAdapter(ctx)

        return UI {
            fitWindowsFrameLayout {
                gridView {
                    lparams {
                        width = matchParent
                        height = matchParent
                    }
                    horizontalSpacing = dip(5)
                    numColumns = 3
                    adapter = mAdapter
                }
            }
        }.view
    }
}

class InvertoryItemAdapter(val context: Context) : BaseAdapter(), AnkoLogger {

    private val map = mutableMapOf<String, InventoryItem>()
    private val list = mutableListOf<String>()
    private fun write(update: () -> Unit) {
        update()
        notifyDataSetChanged()
    }

    override fun getItem(pos: Int) = map[list[pos]]

    fun remove(item: InventoryItem) = write {
        map -= item.id
        list.remove(item.id)
    }

    fun add(item: InventoryItem) = write {
        list.add(item.id)
        map[item.id] = item
    }

    fun update(item: InventoryItem) = write {
        if (!list.contains(item.id)) {
            add(item)
        }
        map[item.name] = item
    }

    override fun getItemId(pos: Int) = pos.toLong()

    override fun getCount() = map.size

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val contentView = view ?: InventoryItemObject().createView(AnkoContext.create(viewGroup.context, viewGroup))
        val item = getItem(position)!!
        contentView.find<TextView>(R.id.tvRss).text = "${item.count}"
        contentView.find<ImageView>(R.id.imgRss).also {
            it.load(context, "images/" + item.img)
        }
        return contentView
    }
}

class InventoryItemObject : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        squaredRelativeLayout {
            backgroundColor = Color.LTGRAY
            imageView {
                id = R.id.imgRss
            }
            textView {
                id = R.id.tvRss
            }
        }
    }
}

class SquaredRelativeLayout(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
            = super.onMeasure(widthMeasureSpec, widthMeasureSpec)
}
