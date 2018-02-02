package nl.pocketquest.pocketquest.views.main.inventory

import nl.pocketquest.pocketquest.game.player.Inventory
import nl.pocketquest.pocketquest.game.player.InventoryListener
import nl.pocketquest.pocketquest.game.player.Item
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.info

class InventoryPresenter(
        val inventoryView: InventoryContract.InventoryView
) : InventoryContract.InventoryPresenter(inventoryView), InventoryListener {
    private var userInventory: Inventory? = null

    override fun onDetach() {
        userInventory?.removeInventoryListener(this)
    }

    override fun onAttach() {
        whenLoggedIn {
            userInventory = Inventory.getUserInventory(it.uid)
                    .also { it.addInventoryListener(this) }
        }
    }

    override fun newInventoryState(item: Item) {
        inventoryView.onInventoryState(item)
    }

    override fun itemAdded(item: Item, prevCount: Long) {
        inventoryView.addItem(item)
    }

    override fun itemRemoved(item: Item) {
        inventoryView.removeItem(item)
    }

    override fun itemClicked(item: Item) {
        info("item clicked: $item")
    }
}
