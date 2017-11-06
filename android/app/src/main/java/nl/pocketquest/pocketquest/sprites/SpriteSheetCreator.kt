package nl.pocketquest.pocketquest.sprites

import android.graphics.Bitmap
import nl.pocketquest.pocketquest.utils.IntPoint
import nl.pocketquest.pocketquest.utils.dimensions
import nl.pocketquest.pocketquest.utils.toIntPoint
import org.jetbrains.anko.AnkoLogger

class SpriteSheetCreator(
        private val spriteSheet: Bitmap,
        private val numberOfFrames: IntPoint
) : AnkoLogger {

    private val spriteSheetDimensions: IntPoint = spriteSheet.dimensions()
    val frames: Sequence<Bitmap> = numberOfFrames.toIntPoint()
            .allSmallerPoints()
            .map(this::subBitMap)

    private fun subBitMap(frameNumber: IntPoint): Bitmap {
        val frameSize = spriteSheetDimensions / numberOfFrames
        val start = frameNumber * frameSize
        return Bitmap.createBitmap(spriteSheet, start.x, start.y, frameSize.x, frameSize.y)
    }
}
