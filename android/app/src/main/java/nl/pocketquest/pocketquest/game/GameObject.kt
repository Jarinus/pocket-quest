package nl.pocketquest.pocketquest.game

import android.graphics.Bitmap
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.properties.Delegates.observable

typealias Consumer<T> = (T) -> Unit
/**
 * A game object should be:
 *  - Displayed on the map with a location and an image
 *  - Able to be interacted with by the user
 * Created by Laurens on 20-10-2017.
 */
open class GameObject(location: LatLng, image: Bitmap) {

    private val changeListeners = mutableListOf<Consumer<GameObject>>()

    fun onChange(consumer: Consumer<GameObject>) = changeListeners.add(consumer)

    var location by observable(location) { _, _, _ -> notifyListeners(changeListeners) }
    var image by observable(image) { _, _, _ -> notifyListeners(changeListeners) }

    protected fun notifyListeners(listeners: List<Consumer<GameObject>>) = listeners.forEach {
        it(this)
    }
}
