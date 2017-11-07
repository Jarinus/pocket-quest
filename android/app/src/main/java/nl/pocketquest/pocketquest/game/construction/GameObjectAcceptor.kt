package nl.pocketquest.pocketquest.game.construction

import com.mapbox.mapboxsdk.geometry.LatLng
import nl.pocketquest.pocketquest.game.IGameObject

/**
 * Created by Laurens on 7-11-2017.
 */
interface GameObjectAcceptor {
    fun gameObjectArrived(key: String, gameObject: IGameObject)
    fun gameObjectMoved(key: String, newLocation: LatLng)
    fun gameObjectDeleted(key: String)
}
