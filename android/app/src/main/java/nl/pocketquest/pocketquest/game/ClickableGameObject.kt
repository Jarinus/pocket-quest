package nl.pocketquest.pocketquest.game

import android.graphics.Bitmap
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Created by Laurens on 6-11-2017.
 */
class ClickableGameObject(location: LatLng, image: Bitmap) : GameObject(location, image) {

    private val onClickListeners = mutableListOf<Consumer<GameObject>>()

    fun onClick(consumer: Consumer<GameObject>) = onClickListeners.add(consumer)

    fun clicked() = notifyListeners(onClickListeners)
}
