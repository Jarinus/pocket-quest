package nl.pocketquest.pocketquest.game.construction

import com.firebase.geofire.GeoLocation
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.ImageResolver

/**
 * Created by Laurens on 7-11-2017.
 */
interface GameObjectCreator {
    fun initialize(imageResolver: ImageResolver)
    fun applicableTo(key: String): Boolean
    suspend fun createGameObject(key: String, location: GeoLocation): IGameObject?
}
