package nl.pocketquest.pocketquest.sprites

import android.graphics.Bitmap
import kotlinx.coroutines.experimental.CommonPool
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.utils.loopAsynchronous
import nl.pocketquest.pocketquest.utils.repeat

class GameObjectAnimator(
        private val gameObject: GameObject,
        frames: Sequence<Bitmap>,
        private val frameDuration: Int
) {

    private val frames = frames
            .repeat()
            .iterator()

    fun start() = loopAsynchronous(CommonPool, sleepDuration = frameDuration) {
        gameObject.image = frames.next()
    }
}
