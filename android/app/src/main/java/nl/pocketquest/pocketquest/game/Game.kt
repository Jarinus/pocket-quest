package nl.pocketquest.pocketquest.game

import android.content.Context
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

/**
 * Created by Laurens on 20-10-2017.
 */

class Game(val context: Context, val map: MapboxMap) {
    private val gameObjects = mutableMapOf<GameObject, Marker>()

    fun addGameObject(gameObject: GameObject) {
        val marker = map.addMarker(MarkerOptions()
                .icon(gameObject.image)
                .position(gameObject.location)
        )
        gameObject.onChange {
            async(UI) {
                marker.icon = it.image
                marker.position = it.location
            }
        }
        gameObjects[gameObject] = marker
    }

    fun removeGameObject(gameObject: GameObject) {
        gameObjects[gameObject]?.also(map::removeMarker)
        gameObjects -= gameObject
    }
}