package nl.pocketquest.pocketquest.utils

import android.view.View
import android.view.View.*

/**
 * Sets this view's visibility to [VISIBLE], if [visible] == true, or [INVISIBLE], if [visible] == false.
 */
fun View.show(visible: Boolean) {
    visibility = if (visible) VISIBLE else INVISIBLE
}

/**
 * Sets this view's visibility to [VISIBLE], if [visible] == true, or [GONE], if [visible] == false.
 */
fun View.showOrHide(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}
