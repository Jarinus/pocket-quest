package nl.pocketquest.pocketquest.views.main.inventory

import android.content.Context
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
import nl.pocketquest.pocketquest.mvp.BaseFragment
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
    private lateinit var mAdapter: InventoryItemAdapter
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

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, itemID: Long) {
        mAdapter.getItem(position)
                ?.let(InventoryItem::toItem)
                ?.also(presenter::itemClicked)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAdapter = InventoryItemAdapter(ctx)
        return UI {
            gridView {
                lparams {
                    width = matchParent
                    height = matchParent
                }
                horizontalSpacing = dip(5)
                verticalSpacing = dip(5)
                numColumns = 3
                adapter = mAdapter
                onItemClickListener = this@InventoryFragment
            }
        }.view
    }
}

class InventoryItemAdapter(val context: Context) : BaseAdapter(), AnkoLogger {

    private val map = mutableMapOf<String, InventoryItem>()
    private val list = mutableListOf<String>()
    private fun write(update: () -> Unit) {
        update()
        info { "Get count = $count" }
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
        map[item.id] = item
    }

    override fun getItemId(pos: Int) = pos.toLong()

    override fun getCount() = map.size

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val contentView = view ?: context.layoutInflater.inflate(R.layout.individual_item_view, viewGroup, false)
        val item = getItem(position)!!
        contentView.find<TextView>(R.id.tvRss).text = "${item.count}"
        contentView.find<ImageView>(R.id.imgRss).also {
            it.load(context, "images/" + item.img)
        }
        return contentView
    }
}
