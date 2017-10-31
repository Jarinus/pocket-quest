package nl.pocketquest.pocketquest.game

import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlin.properties.Delegates.observable

typealias Consumer<T> = suspend CoroutineScope.(T) -> Unit
/**
 * A game object should be:
 *  - Displayed on the map with a location and an image
 *  - Able to be interacted with by the user
 *  - Able to change its state based on a database update
 * Created by Laurens on 20-10-2017.
 */
open class GameObject(location: LatLng, image: Icon) {

    private val listeners = mutableListOf<Consumer<GameObject>>()
    fun onChange(consumer: Consumer<GameObject>) = listeners.add(consumer)

    var location by observable(location) { _, _, _ -> notifyListeners() }
    var image by observable(image) { _, _, _ -> notifyListeners() }

    private fun notifyListeners() = listeners.forEach {
        launch(UI) {
            it(this@GameObject)
        }
    }
}
