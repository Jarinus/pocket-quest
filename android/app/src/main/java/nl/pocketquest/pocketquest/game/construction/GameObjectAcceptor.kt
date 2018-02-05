package nl.pocketquest.pocketquest.game.construction

import com.google.android.gms.maps.model.LatLng
import nl.pocketquest.pocketquest.game.IGameObject

interface GameObjectAcceptor {
    fun gameObjectArrived(key: String, gameObject: IGameObject)
    fun gameObjectMoved(key: String, newLocation: LatLng)
    fun gameObjectDeleted(key: String)
}
