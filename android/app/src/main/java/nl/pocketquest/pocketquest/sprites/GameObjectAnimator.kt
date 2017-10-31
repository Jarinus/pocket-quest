package nl.pocketquest.pocketquest.sprites

import android.content.Context
import android.graphics.Bitmap
import com.mapbox.mapboxsdk.annotations.IconFactory
import kotlinx.coroutines.experimental.CommonPool
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.utils.loopAsynchronous
import nl.pocketquest.pocketquest.utils.repeat

class GameObjectAnimator(
        private val context: Context,
        private val gameObject: GameObject,
        frames: Sequence<Bitmap>,
        private val frameDuration: Int
) {
    private val frames = frames.map { IconFactory.getInstance(context).fromBitmap(it) }
            .repeat()
            .iterator()

    fun start() = loopAsynchronous(CommonPool, sleepDuration = frameDuration) {
        gameObject.image = frames.next()
    }
}
