package nl.pocketquest.pocketquest.game.resource

import com.firebase.geofire.GeoLocation
import nl.pocketquest.pocketquest.game.ClickableGameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.construction.GameObjectCreator
import nl.pocketquest.pocketquest.game.entities.Entities
import nl.pocketquest.pocketquest.game.entities.FBResourceInstance
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.readAsync
import nl.pocketquest.pocketquest.utils.toLatLng

/**
 * Created by Laurens on 7-11-2017.
 */
class ResourceInstanceCreator : GameObjectCreator {

    private lateinit var imageResolver: ImageResolver

    override fun initialize(imageResolver: ImageResolver) {
        this.imageResolver = imageResolver
    }

    override fun applicableTo(key: String): Boolean {
        return key.startsWith("resource_instances")
    }

    suspend override fun createGameObject(key: String, location: GeoLocation): IGameObject? {
        val resource = DATABASE.getReference(key).readAsync<FBResourceInstance>()
        val resourceNode = Entities.resource_nodes[resource.type] ?: return null
        val image = imageResolver.resolveImage(resourceNode?.icon)
        val gameObject = ClickableGameObject(location.toLatLng(), image)
        return ResourceInstance(key, gameObject, resourceNode)
    }
}
