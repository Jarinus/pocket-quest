package nl.pocketquest.pocketquest.sprites

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Created by Laurens on 20-10-2017.
 */

data class DoublePoint(val x: Double, val y: Double)

fun Bitmap.padded(padding: DoublePoint): Bitmap {
    val newBitMap = Bitmap.createBitmap((width * padding.x).toInt(), (height * padding.y).toInt(), config)
    val canvas = Canvas(newBitMap)
    canvas.drawBitmap(this, 0f, 0f, null)
    return newBitMap
}
