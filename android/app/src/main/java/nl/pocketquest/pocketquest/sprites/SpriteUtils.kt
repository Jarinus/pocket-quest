package nl.pocketquest.pocketquest.sprites

import android.graphics.Bitmap
import android.graphics.Canvas
import nl.pocketquest.pocketquest.utils.Point
import nl.pocketquest.pocketquest.utils.times


fun Bitmap.padded(padding: Point) = Bitmap.createBitmap(
        (width * padding.x).toInt(), (height * padding.y).toInt(), config
).also { Canvas(it).drawBitmap(this, 0f, 0f, null) }
