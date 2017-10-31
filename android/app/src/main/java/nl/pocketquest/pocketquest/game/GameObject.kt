package nl.pocketquest.pocketquest.game

import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.properties.Delegates.observable

/**
 * A game object should be:
 *  - Displayed on the map with a location and an image
 *  - Able to be interacted with by the user
 *  - Able to change its state based on a database update
 * Created by Laurens on 20-10-2017.
 */

typealias Consumer<T> = (T) -> Unit
class GameObject(location: LatLng, image: Icon) {

    private val listeners = mutableListOf<Consumer<GameObject>>()
    fun onChange(consumer: Consumer<GameObject>) = listeners.add(consumer)

    var location by observable(location) { property, oldValue, newValue ->
        listeners.forEach { it(this) }
    }
    var image by observable(image) { property, oldValue, newValue ->
        listeners.forEach { it(this) }
    }

}