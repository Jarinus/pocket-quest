package nl.pocketquest.pocketquest.game.player

import com.google.firebase.database.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import nl.pocketquest.pocketquest.game.entities.Entities
import nl.pocketquest.pocketquest.game.entities.FBItem
import nl.pocketquest.pocketquest.utils.DATABASE
import org.jetbrains.anko.AnkoLogger

interface InventoryListener {
    fun newInventoryState(item: Item)
    fun itemAdded(item: Item, prevCount: Long)
    fun itemRemoved(item: Item)
}

data class Item(val itemName: String, val itemCount: Long) {
    suspend fun getItemProperties(): FBItem? = Entities.getItem(itemName)
}

class Inventory(val ref: DatabaseReference) : AnkoLogger {
    private val items = mutableMapOf<String, Long>()
    private val inventoryListeners = mutableListOf<InventoryListener>()
    private val inventoryUpdater = InventoryUpdater(this)
    private var initialLoad = false

    init {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = Unit

            override fun onDataChange(p0: DataSnapshot?) {
                initialLoad = true
            }
        })
    }

    fun setItem(itemName: String, count: Long) {
        val item = Item(itemName, count)
        if (count == 0L) {
            removeItem(item)
            return
        }
        val prevItemCount = items[itemName] ?: 0L
        items[itemName] = count
        inventoryListeners.forEach {
            it.newInventoryState(item)
            if (initialLoad) {
                it.itemAdded(item, prevItemCount)
            }
        }
    }

    private fun removeItem(item: Item) {
        items -= item.itemName
        inventoryListeners.forEach {
            it.itemRemoved(item)
        }
    }

    fun addInventoryListener(inventoryListener: InventoryListener) {
        inventoryListeners.add(inventoryListener)
        items.forEach { (k, v) ->
            inventoryListener.newInventoryState(Item(k, v))
        }
        if (inventoryListeners.size == 1) ref.addChildEventListener(inventoryUpdater)
    }
    fun removeInventoryListener(inventoryListener: InventoryListener) {
        inventoryListeners.remove(inventoryListener)
        if (inventoryListeners.size == 0) ref.removeEventListener(inventoryUpdater)
    }
    companion object {
        private var userInventory: Inventory? = null
        fun getUserInventory(uid: String): Inventory {
            return userInventory ?: Inventory(DATABASE.getReference("user_items/$uid/backpack"))
                    .also { userInventory = it }
        }
    }
}

class InventoryUpdater(val inventory: Inventory) : ChildEventListener, AnkoLogger {

    private fun childChangeOrAdd(snapshot: DataSnapshot) {
        val itemName = snapshot.key
        val itemCount = snapshot.getValue(Long::class.java) ?: 0
        inventory.setItem(itemName, itemCount)
    }

    override fun onCancelled(p0: DatabaseError?) = Unit

    override fun onChildMoved(snapshot: DataSnapshot, oldKey: String?) = Unit

    override fun onChildChanged(snapshot: DataSnapshot, oldKey: String?) = childChangeOrAdd(snapshot)

    override fun onChildAdded(snapshot: DataSnapshot, oldKey: String?) = childChangeOrAdd(snapshot)

    
    override fun onChildRemoved(snapshot: DataSnapshot) {
        val itemName = snapshot.key
        inventory.setItem(itemName, 0)
    }
}
