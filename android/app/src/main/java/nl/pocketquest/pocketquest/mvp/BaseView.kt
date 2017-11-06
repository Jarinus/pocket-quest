package nl.pocketquest.pocketquest.mvp

import android.graphics.Bitmap

/**
 * Created by Laurens on 6-11-2017.
 */
interface BaseView {
    fun decodeResource(resourceID: Int): Bitmap
    fun displayToast(message: String)
}
