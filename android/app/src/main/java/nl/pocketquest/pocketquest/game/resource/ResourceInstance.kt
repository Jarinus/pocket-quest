package nl.pocketquest.pocketquest.game.resource

import nl.pocketquest.pocketquest.game.ClickableGameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.FBResourceNode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by Laurens on 7-11-2017.
 */
class ResourceInstance(
        val resourceID: String,
        private val clickableGameObject: ClickableGameObject,
        private val resourceNode: FBResourceNode
) : IGameObject by clickableGameObject, AnkoLogger {

    init {
        clickableGameObject.onClick {
            info { "The user would like to gather resources from $resourceID which is a ${resourceNode.name}" }
        }
    }
}
