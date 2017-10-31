package nl.pocketquest.pocketquest.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.mapbox.mapboxsdk.annotations.IconFactory


/**
 * Created by thijs on 20-10-2017.
 */
fun Bitmap.dimensions() = width xy height

fun Drawable.toBitmap(): Bitmap {
    var bitmap: Bitmap? = null

    if (this is BitmapDrawable) {
        if (this.bitmap != null) {
            return this.bitmap
        }
    }

    if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
    } else {
        bitmap = Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    this.draw(canvas)
    return bitmap
}

fun Activity.loadImage(@DrawableRes image: Int) = IconFactory.getInstance(this).fromResource(image)
fun Context.decodeResource(@DrawableRes rss: Int) = BitmapFactory.decodeResource(resources, rss)