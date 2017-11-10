package nl.pocketquest.pocketquest.game.construction

import com.firebase.geofire.GeoLocation
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.ImageResolver

interface GameObjectCreator {
    fun initialize(imageResolver: ImageResolver)
    fun applicableTo(key: String): Boolean
    suspend fun createGameObject(key: String, location: GeoLocation): IGameObject?
}
