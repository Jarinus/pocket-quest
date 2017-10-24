package nl.pocketquest.pocketquest.sprites

import android.content.Context
import android.graphics.Bitmap
import com.mapbox.mapboxsdk.annotations.IconFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.pocketquest.pocketquest.game.GameObject


class GameObjectAnimator(
        private val context: Context,
        private val gameObject: GameObject,
        frames: Sequence<Bitmap>,
        private val animationDuration: Int) {
    private var index = 0
    private val frames = frames.map { IconFactory.getInstance(context).fromBitmap(it) }.toList()

    fun start() = async(CommonPool) {
        while (isActive) {
            gameObject.image = frames[index]
            index = (index + 1) % frames.size
            delay(animationDuration.toLong())
        }
    }
}