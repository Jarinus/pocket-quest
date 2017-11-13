package nl.pocketquest.pocketquest.mvp

import android.graphics.Bitmap
import org.jetbrains.anko.AnkoLogger

interface BaseView : AnkoLogger {
    fun decodeResource(resourceID: Int): Bitmap
    fun displayToast(message: String)
}
