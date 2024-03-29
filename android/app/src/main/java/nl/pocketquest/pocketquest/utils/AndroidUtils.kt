package nl.pocketquest.pocketquest.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

fun Bitmap.dimensions() = width xy height
fun Drawable.toBitmap(): Bitmap = (this as? BitmapDrawable)?.bitmap ?: drawBitmap()
private fun Drawable.drawBitmap(): Bitmap {
    val width = maxOf(intrinsicWidth, 1)
    val height = maxOf(intrinsicHeight, 1)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun Context.decodeResource(@DrawableRes rss: Int): Bitmap = BitmapFactory.decodeResource(resources, rss)

fun FragmentManager.doTransaction(transaction: FragmentTransaction.() -> Unit)
        = beginTransaction().also(transaction).commit()

fun FragmentManager.doTransactionAllowingStateLoss(transaction: FragmentTransaction.() -> Unit)
        = beginTransaction().also(transaction).commitAllowingStateLoss()
