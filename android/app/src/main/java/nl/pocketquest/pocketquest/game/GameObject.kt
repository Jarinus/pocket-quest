package nl.pocketquest.pocketquest.game

import android.graphics.Bitmap
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.properties.Delegates.observable

typealias Consumer<T> = (T) -> Unit
interface IGameObject {
    var location: LatLng
    var image: Bitmap
    fun onChange(consumer: Consumer<GameObject>): Boolean
}

/**
 * A game object should be:
 *  - Displayed on the map with a location and an image
 *  - Able to be interacted with by the user
 * Created by Laurens on 20-10-2017.
 */
open class GameObject(location: LatLng, image: Bitmap) : IGameObject {

    private val changeListeners = mutableListOf<Consumer<GameObject>>()

    override fun onChange(consumer: Consumer<GameObject>) = changeListeners.add(consumer)

    override var location by observable(location) { _, _, _ -> notifyListeners(changeListeners) }
    override var image by observable(image) { _, _, _ -> notifyListeners(changeListeners) }

    protected fun notifyListeners(listeners: List<Consumer<GameObject>>) = listeners.forEach {
        it(this)
    }
}
