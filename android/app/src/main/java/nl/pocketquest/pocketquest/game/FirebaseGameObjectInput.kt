package nl.pocketquest.pocketquest.game

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.game.construction.GameObjectAcceptor
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.game.resource.ResourceInstanceCreator
import nl.pocketquest.pocketquest.utils.toGeoLocation
import nl.pocketquest.pocketquest.utils.toLatLng
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class FirebaseGameObjectInput(
        queryCenter: LatLng,
        private val gameObjectAcceptor: GameObjectAcceptor,
        private val imageResolver: ImageResolver
) : GeoQueryEventListener, AnkoLogger {
    var queryCenter: LatLng = queryCenter
        set(value) {
            field = value
            query.center = value.toGeoLocation()
            info { "Setting new value for qeoQuery" }
        }

    private val geoFire = GeoFire(FirebaseDatabase.getInstance().getReference("locations"))
    private val query: GeoQuery
    private val objectCreators = listOf(
            ResourceInstanceCreator()
    )

    init {
        query = geoFire.queryAtLocation(queryCenter.toGeoLocation(), 0.2)
        query.addGeoQueryEventListener(this)
        objectCreators.forEach { it.initialize(imageResolver) }
    }

    override fun onGeoQueryReady() = Unit

    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        info { "Key entered:  $key on location $location" }
        if (key == null || location == null) return
        val escapedKey = escapeKey(key)
        async(CommonPool) {
            objectCreators
                    .filter { it.applicableTo(escapedKey) }
                    .mapNotNull { it.createGameObject(escapedKey, location) }
                    .forEach { gameObjectAcceptor.gameObjectArrived(escapedKey, it) }
        }
    }

    private fun escapeKey(key: String) = key.replace('-', '/')

    override fun onKeyMoved(key: String?, location: GeoLocation?) {
        if (key != null && location != null) {
            gameObjectAcceptor.gameObjectMoved(escapeKey(key), location.toLatLng())
        }
    }

    override fun onKeyExited(key: String?) {
        key
                ?.let(this::escapeKey)
                ?.also(gameObjectAcceptor::gameObjectDeleted)
    }

    override fun onGeoQueryError(error: DatabaseError?) = Unit
}
