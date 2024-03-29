package nl.pocketquest.pocketquest.views.main.map.overlay

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.player.Inventory
import nl.pocketquest.pocketquest.game.player.InventoryListener
import nl.pocketquest.pocketquest.game.player.Item
import nl.pocketquest.pocketquest.game.player.Status
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.listen
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.wtf

class MapOverlayPresenter(mapOverlayView: MapOverlayContract.MapOverlayView) :
        MapOverlayContract.MapOverlayPresenter(mapOverlayView), InventoryListener {

    var userInventory: Inventory? = null
    override fun onDetach() {
        userInventory?.removeInventoryListener(this)
    }

    override fun onAttach() {
        whenLoggedIn {
            userInventory = Inventory.getUserInventory(it.uid).also {
                it.addInventoryListener(this)
            }
            initializeGatheringStatus(it)
        }
    }

    private fun initializeGatheringStatus(it: FirebaseUser) {
        DATABASE.getReference("users/${it.uid}/status").listen(this::onUserStatusChange)
        async(CommonPool) {
            try {
                view.setRightCornerImage(view.getImageResolver().resolveImage("items/Hatchet_1.png"))
            } catch (e: Exception) {
                wtf(e.getStackTraceString())
            }
        }
    }

    fun onUserStatusChange(newStatus: String) = when (newStatus) {
        Status.GATHERING.firebaseName -> view.setRightCornerImageVisibility(true)
        else -> view.setRightCornerImageVisibility(false)
    }

    override fun newInventoryState(item: Item) = Unit

    override fun itemAdded(item: Item, prevCount: Long) {
        val addition = item.itemCount - prevCount
        if (addition <= 0) return
        async(CommonPool) {
            item.getItemProperties()?.name?.also {
                view.displayNotification("$it +$addition")
            }
        }
    }

    override fun itemRemoved(item: Item) = Unit
}
