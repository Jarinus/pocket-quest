package nl.pocketquest.pocketquest.views.main.inventory

import nl.pocketquest.pocketquest.game.player.Inventory
import nl.pocketquest.pocketquest.game.player.InventoryListener
import nl.pocketquest.pocketquest.game.player.Item
import nl.pocketquest.pocketquest.utils.whenLoggedIn

class InventoryPresenter(
        val inventoryView: InventoryContract.InventoryView
) : InventoryContract.InventoryPresenter(inventoryView), InventoryListener {
    override fun attached() {
        whenLoggedIn {
            Inventory.getUserInventory(it.uid).addInventoryListener(this)
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
        view.displayToast("item clicked: $item")
    }
}
