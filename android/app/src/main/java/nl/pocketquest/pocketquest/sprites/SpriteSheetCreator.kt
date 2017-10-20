package nl.pocketquest.pocketquest.sprites

import android.graphics.Bitmap
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * Created by Laurens on 20-10-2017.
 */
data class Point(val x: Int, val y: Int) {
    operator fun div(other: Point) = Point(x / other.x, y / other.y)
    operator fun times(other: Point) = Point(x * other.x, y * other.y)
    fun allSmallerPoints() =
            (0..y - 1).asSequence()
                    .flatMap { y ->
                        (0..x - 1).asSequence()
                                .map { x -> Point(x, y) }
                    }

}

fun Bitmap.dimensions() = Point(width, height)
class SpriteSheetCreator(private val spriteSheet: Bitmap,
                         private val numberOfFrames: Point) : AnkoLogger {


    private val spriteSheetDimensions: Point
    val frames: List<Bitmap>

    init {
        spriteSheetDimensions = spriteSheet.dimensions()
        frames = numberOfFrames.allSmallerPoints()
                .map(this::subBitMap)
                .toList()
    }

    private fun subBitMap(frameNumber: Point): Bitmap {
        val frameSize = spriteSheetDimensions / numberOfFrames
        val start = frameNumber * frameSize
        return Bitmap.createBitmap(spriteSheet, start.x, start.y, frameSize.x, frameSize.y)
    }
}