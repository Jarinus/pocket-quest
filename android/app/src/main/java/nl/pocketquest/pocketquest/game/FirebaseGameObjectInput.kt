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
import nl.pocketquest.pocketquest.game.entities.FBResourceInstance
import nl.pocketquest.pocketquest.game.entities.FBResourceNode
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.readAsync
import nl.pocketquest.pocketquest.utils.toGeoLocation
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by Laurens on 6-11-2017.
 */
class FirebaseGameObjectInput(queryCenter: LatLng) : GeoQueryEventListener, AnkoLogger {
    var queryCenter: LatLng = queryCenter
        set(value) {
            field = value
            query.center = value.toGeoLocation()
        }
    private val geoFire = GeoFire(FirebaseDatabase.getInstance().getReference("locations/geofire"))
    private val query: GeoQuery

    init {
        query = geoFire.queryAtLocation(queryCenter.toGeoLocation(), 0.2)
        query.addGeoQueryEventListener(this)
    }

    override fun onGeoQueryReady() = Unit

    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        if (key == null || location == null) {
            return
        }
        info { "Key arrived $key" }
        if (key.startsWith("resource_instances")) {
            async(CommonPool) {
                val resource = DATABASE.getReference(key).readAsync<FBResourceInstance>()
                val resourceNode = DATABASE.getReference(resource.type).readAsync<FBResourceNode>()
            }
        }
    }

    override fun onKeyMoved(key: String?, location: GeoLocation?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onKeyExited(key: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGeoQueryError(error: DatabaseError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
