package nl.pocketquest.pocketquest.sprites

import android.graphics.Bitmap
import android.graphics.Canvas
import nl.pocketquest.pocketquest.utils.Point
import nl.pocketquest.pocketquest.utils.times


fun Bitmap.padded(padding: Point) = createScaledEmptyBitmap(padding)
        .also(this::drawInto)

private fun Bitmap.drawInto(other: Bitmap) = Canvas(other)
        .drawBitmap(this, 0f, 0f, null)

private fun Bitmap.createScaledEmptyBitmap(padding: Point) = Bitmap.createBitmap(
        (width * padding.x).toInt(),
        (height * padding.y).toInt(),
        config
)

