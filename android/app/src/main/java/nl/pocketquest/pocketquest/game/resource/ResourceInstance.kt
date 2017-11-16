package nl.pocketquest.pocketquest.game.resource

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.Clickable
import nl.pocketquest.pocketquest.game.ClickableGameObject
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.FBResourceGatherRequest
import nl.pocketquest.pocketquest.game.entities.FBResourceNode
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.info
import org.jetbrains.anko.wtf

enum class Status { EMPTY, HAS_RESOURCES }
class ResourceInstance(
        val resourceID: String,
        private val clickableGameObject: ClickableGameObject,
        private val resourceNode: FBResourceNode,
        private val imageResolver: ImageResolver
) : IGameObject by clickableGameObject, Clickable<GameObject> by clickableGameObject, AnkoLogger {

    private val resources_left = mutableMapOf<String, Long>()
    private var status = Status.HAS_RESOURCES

    init {
        addOnClick()
        addResourceCountListener()
    }

    private fun addResourceCountListener() {
        DATABASE.getReference("$resourceID/resources_left").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) = Unit

            override fun onChildMoved(snapshot: DataSnapshot?, oldKey: String?) = Unit

            override fun onChildChanged(snapshot: DataSnapshot?, oldKey: String?) {
                snapshot?.also(this@ResourceInstance::setResourceLeft)
            }

            override fun onChildAdded(snapshot: DataSnapshot?, oldKey: String?) {
                snapshot?.also(this@ResourceInstance::setResourceLeft)
            }

            override fun onChildRemoved(snapshot: DataSnapshot?) {
                resources_left -= snapshot?.key ?: return
            }
        })
    }

    fun setResourceLeft(snapshot: DataSnapshot) {
        setResourceLeft(snapshot.key, snapshot.getValue(Long::class.java) ?: return)
    }

    fun setResourceLeft(resource: String, count: Long) {
        resources_left[resource] = count
        val newResourceStatus = when (resources_left.values.all { it == 0L }) {
            true -> Status.EMPTY
            false -> Status.HAS_RESOURCES
        }
        updateResourcesStatus(newResourceStatus)
    }

    private fun updateResourcesStatus(newResourceStatus: Status) {
        if (newResourceStatus != status) {
            status = newResourceStatus
            val newIcon = when (newResourceStatus) {
                Status.EMPTY -> resourceNode.icon.substringBeforeLast(".png") + "_empty" + ".png"
                Status.HAS_RESOURCES -> resourceNode.icon
            }
            async(CommonPool) {
                try {
                    clickableGameObject.image = imageResolver.resolveImage(newIcon)
                } catch (e: Exception) {
                    wtf(e.getStackTraceString())
                }
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
                DATABASE.getReference("requests/resource_gathering").push()
                        .setValue(FBResourceGatherRequest(resourceInstanceID, user_id = it.uid))
            }
        }
    }
}
