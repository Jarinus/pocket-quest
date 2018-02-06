package nl.pocketquest.pocketquest.views.main.inventory

import android.content.Context
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.entities.load
import nl.pocketquest.pocketquest.game.player.Item
import nl.pocketquest.pocketquest.utils.squaredImageView
import nl.pocketquest.pocketquest.utils.withSuffix
import nl.pocketquest.pocketquest.views.BaseFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.ctx

data class InventoryItem(val id: String, val name: String, val count: Long, val img: String)

private fun Item.toInventoryItem(itemConsumer: (InventoryItem) -> Unit) {
    async(UI) {
        try {
            val (name, icon, _) = getItemProperties()!!
            val item = InventoryItem(itemName, name, itemCount, icon)
            itemConsumer(item)
        } catch (e: Exception) {
            Log.wtf("inventory", e.getStackTraceString())
        }
    }
}

private fun InventoryItem.toItem() = Item(name, count)

class InventoryFragment : BaseFragment(), InventoryContract.InventoryView, AdapterView.OnItemClickListener {
    private val presenter: InventoryContract.InventoryPresenter = InventoryPresenter(this)
    private val mAdapter by lazy { InventoryItemAdapter(ctx) }
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
        presenter.onAttach()
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, itemID: Long) {
        mAdapter.getItem(position)
                ?.let(InventoryItem::toItem)
                ?.also(presenter::itemClicked)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return UI {
            gridView {
                adapter = mAdapter
                numColumns = 4

                onItemClickListener = this@InventoryFragment

                padding = dip(8)

                lparams {
                    width = matchParent
                    height = matchParent

                    horizontalSpacing = dip(8)
                    verticalSpacing = dip(8)
                }
            }
        }.view
    }
}

class InventoryItemAdapter(val context: Context) : BaseAdapter(), AnkoLogger {

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
        if (item.id !in list) list.add(item.id)
        map[item.id] = item
    }

    fun update(item: InventoryItem) = write {
        if (item.id !in list) add(item)
        map[item.id] = item
    }

    override fun getItemId(pos: Int) = pos.toLong()

    override fun getCount() = map.size

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val contentView = view ?: with(context) {
            relativeLayout {
                backgroundColor = Color.parseColor("#bbdefb")

                squaredImageView {
                    id = R.id.imgRss
                }.lparams {
                    width = matchParent
                    height = matchParent
                }

                textView("0") {
                    id = R.id.tvRss

                    textSize = 16f
                    textColor = WHITE
                    typeface = Typeface.DEFAULT_BOLD

                    setShadowLayer(4f, 0f, 0f, BLACK)
                }.lparams {
                    alignParentBottom()
                    alignParentRight()
                }

                lparams {
                    padding = dip(4)
                }
            }
        }
        val item = getItem(position)!!
        contentView.find<ImageView>(R.id.imgRss).apply {
            load(context, item.img)
        }

        contentView.find<TextView>(R.id.tvRss).apply {
            text = item.count.withSuffix()
        }
        return contentView
    }
}
