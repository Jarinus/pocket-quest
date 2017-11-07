package nl.pocketquest.pocketquest.game.resource

import nl.pocketquest.pocketquest.game.Clickable
import nl.pocketquest.pocketquest.game.ClickableGameObject
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.FBResourceGatherRequest
import nl.pocketquest.pocketquest.game.entities.FBResourceNode
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.whenLoggedIn
import org.jetbrains.anko.AnkoLogger

class ResourceInstance(
        val resourceID: String,
        private val clickableGameObject: ClickableGameObject,
        private val resourceNode: FBResourceNode
) : IGameObject by clickableGameObject, Clickable<GameObject> by clickableGameObject, AnkoLogger {

    private var clicked = false

    init {
        clickableGameObject.onClick {
            if (clicked) {
                return@onClick
            }
            clicked = true
            whenLoggedIn {
                val resourceInstanceID = if ("/" in resourceID) {
                    resourceID.substringAfterLast("/")
                } else {
                    resourceID
                }
                DATABASE.getReference("requests/resource_gathering/${it.uid}").push()
                        .setValue(FBResourceGatherRequest(resourceInstanceID))
            }
        }
    }
}
