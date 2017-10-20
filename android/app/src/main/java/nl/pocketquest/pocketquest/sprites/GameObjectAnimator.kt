package nl.pocketquest.pocketquest.sprites

import android.content.Context
import android.graphics.Bitmap
import com.mapbox.mapboxsdk.annotations.IconFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.pocketquest.pocketquest.game.GameObject


/**
 * Created by Laurens on 20-10-2017.
 */
class GameObjectAnimator(val context: Context, val gameObject: GameObject, frames: List<Bitmap>, val animationDuration: Int) {

    private var index = 0
    private val frames = frames.map { IconFactory.getInstance(context).fromBitmap(it) }

    fun start() = async(CommonPool) {
        while (true) {
            gameObject.image = frames[index]
            index = (index + 1) % frames.size
            delay(animationDuration.toLong())
        }
    }
}