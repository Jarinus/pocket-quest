package nl.pocketquest.pocketquest.views.main.inventory

import nl.pocketquest.pocketquest.game.player.Item
import nl.pocketquest.pocketquest.views.BasePresenter
import nl.pocketquest.pocketquest.views.BaseView

class InventoryContract {
    interface InventoryView : BaseView {
        fun addItem(item: Item)
        fun removeItem(item: Item)
        fun onInventoryState(item: Item)
    }

    abstract class InventoryPresenter(inventoryView: InventoryView) : BasePresenter<InventoryView>(inventoryView) {
        abstract fun itemClicked(item: Item)
    }
}
