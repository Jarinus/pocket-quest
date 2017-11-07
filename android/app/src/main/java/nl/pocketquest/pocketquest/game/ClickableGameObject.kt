package nl.pocketquest.pocketquest.game

import android.graphics.Bitmap
import com.mapbox.mapboxsdk.geometry.LatLng

interface Clickable<T> {
    fun onClick(consumer: Consumer<T>): Boolean
    fun clicked()
}

/**
 * Created by Laurens on 6-11-2017.
 */
class ClickableGameObject(location: LatLng, image: Bitmap) : GameObject(location, image), Clickable<GameObject> {

    private val onClickListeners = mutableListOf<Consumer<GameObject>>()

    override fun onClick(consumer: Consumer<GameObject>) = onClickListeners.add(consumer)

    override fun clicked() = notifyListeners(onClickListeners)
}
