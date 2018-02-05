package nl.pocketquest.pocketquest.game

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

interface Clickable<out T> {
    fun onClick(consumer: Consumer<T>): Boolean
    fun clicked()
}

class ClickableGameObject(location: LatLng, image: Bitmap) : GameObject(location, image), Clickable<GameObject> {

    private val onClickListeners = mutableListOf<Consumer<GameObject>>()

    override fun onClick(consumer: Consumer<GameObject>) = onClickListeners.add(consumer)

    override fun clicked() = notifyListeners(onClickListeners)
}
