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
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ResourceInstanceCreator : GameObjectCreator, AnkoLogger {

    private lateinit var imageResolver: ImageResolver

    override fun initialize(imageResolver: ImageResolver) {
        this.imageResolver = imageResolver
    }

    override fun applicableTo(key: String) = key.startsWith("resource_instances")

    suspend override fun createGameObject(key: String, location: GeoLocation): IGameObject? {
        info { "Creating a new gameobject from key $key" }
        val resource = DATABASE.getReference(key).readAsync<FBResourceInstance>()
        info { "Loaded resource $resource" }
        val resourceNode = Entities.getResourceNodes()[resource.type] ?: return null
        info { "Loaded resourceNode $resourceNode" }
        val gameObject = ClickableGameObject(location.toLatLng(), ResourceInstance.EMPTY_ICON)
        return ResourceInstance(key, gameObject, resourceNode, imageResolver)
    }
}
