package nl.pocketquest.pocketquest.game.resource

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.Clickable
import nl.pocketquest.pocketquest.game.ClickableGameObject
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.FBResourceGatherRequest
import nl.pocketquest.pocketquest.game.entities.FBResourceNode
import nl.pocketquest.pocketquest.game.entities.FirebaseCounter
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.info
import org.jetbrains.anko.wtf

private enum class Status { UNCERTAIN, EMPTY, HAS_RESOURCES }

class ResourceInstance(
        val resourceID: String,
        private val clickableGameObject: ClickableGameObject,
        private val resourceNode: FBResourceNode,
        private val imageResolver: ImageResolver
) : IGameObject by clickableGameObject, Clickable<GameObject> by clickableGameObject, AnkoLogger {

    private var status = Status.UNCERTAIN
    private val counter: FirebaseCounter
    private var resourceCounts = mapOf<String, Long>()

    init {
        addOnClick()
        counter = FirebaseCounter(DATABASE.getReference("$resourceID/resources_left"))
        counter.addListener(::lookAtNewCounts)
    }

    private fun lookAtNewCounts(counts: Map<String, Long>) {
        this.resourceCounts = counts
        val newResourcesStatus = when (counts.values.all { it == 0L }) {
            true -> Status.EMPTY
            false -> Status.HAS_RESOURCES
        }
        updateResourcesStatus(newResourcesStatus)
    }

    private fun updateResourcesStatus(newResourcesStatus: Status) {
        if (newResourcesStatus != status) {
            status = newResourcesStatus
            updateImageIcon(newResourcesStatus)
        }
    }

    private fun updateImageIcon(newResourcesStatus: Status) {
        async(CommonPool) {
            val newIcon = when (newResourcesStatus) {
                Status.UNCERTAIN -> EMPTY_ICON
                Status.EMPTY -> imageResolver.resolveImage(resourceNode.icon_empty)
                Status.HAS_RESOURCES -> imageResolver.resolveImage(resourceNode.icon)
            }
            try {
                clickableGameObject.image = newIcon
            } catch (e: Exception) {
                wtf(e.getStackTraceString())
            }
        }
    }

    private fun addOnClick() {
        clickableGameObject.onClick {
            info { "This resource was clicked" }
            whenLoggedIn {
                val resourceInstanceID = if ("/" in resourceID) {
                    resourceID.substringAfterLast("/")
                } else {
                    resourceID
                }
                val resourceID = resourceCounts.filter {
                    it.value > 0
                }.keys.firstOrNull() ?: return@whenLoggedIn
                DATABASE.getReference("requests/resource_gathering").push()
                        .setValue(FBResourceGatherRequest(
                                resourceInstanceID,
                                user_id = it.uid,
                                resource_id = resourceID
                        ))
            }
        }
    }

    companion object {
        val EMPTY_ICON: Bitmap = createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    }
}
