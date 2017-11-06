package nl.pocketquest.pocketquest.game

import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.maps.MapboxMap
import nl.pocketquest.pocketquest.utils.addMarker

class Game(private val map: MapboxMap) {
    private val gameObjects = mutableMapOf<GameObject, Marker>()
    private val markers = mutableMapOf<Marker, GameObject>()

    init {
        map.setOnMarkerClickListener {
            markers[it]?.clicked()
            true
        }
    }

    operator fun plusAssign(gameObject: GameObject) {
        val marker = map.addMarker {
            icon = gameObject.image
            position = gameObject.location
        }
        gameObject.onChange {
            marker.icon = it.image
            marker.position = it.location
        }
        gameObjects[gameObject] = marker
        markers[marker] = gameObject
    }

    operator fun minusAssign(gameObject: GameObject) {
        gameObjects[gameObject]?.also(map::removeMarker)
        gameObjects -= gameObject
    }
}
