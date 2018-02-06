package nl.pocketquest.pocketquest.sprites

import android.graphics.Bitmap
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.utils.repeat
import kotlin.system.measureTimeMillis

class GameObjectAnimator(
        frames: Sequence<Bitmap>,
        private val frameDuration: Int
) {
    var gameObject: GameObject? = null
    private var active = true
    private val frames = frames
            .repeat()
            .iterator()

    fun start() = async(CommonPool) {
        while (active) {
            measureTimeMillis {
                gameObject?.image = frames.next()
            }.also {
                delay(maxOf(0, frameDuration - it))
            }
        }
        cleanUp()
    }

    private fun cleanUp() {
        frames.forEach { it.recycle() }
    }

    fun close() {
        active = false
    }
}
