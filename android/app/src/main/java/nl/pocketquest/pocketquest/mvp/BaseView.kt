package nl.pocketquest.pocketquest.mvp

import android.graphics.Bitmap

interface BaseView {
    fun decodeResource(resourceID: Int): Bitmap
    fun displayToast(message: String)
}
