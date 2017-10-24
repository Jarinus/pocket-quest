package nl.pocketquest.pocketquest.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import com.mapbox.mapboxsdk.annotations.IconFactory

/**
 * Created by thijs on 20-10-2017.
 */
fun Bitmap.dimensions() = width xy height

fun createBitmap(bitmap: Bitmap, start: IntPoint, frameSize: IntPoint)
        = Bitmap.createBitmap(bitmap, start.x, start.y, frameSize.x, frameSize.y)

fun Activity.loadImage(@DrawableRes image: Int) = IconFactory.getInstance(this).fromResource(image)
fun Context.decodeRss(@DrawableRes rss: Int) = BitmapFactory.decodeResource(resources, rss)