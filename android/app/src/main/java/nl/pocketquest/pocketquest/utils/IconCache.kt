package nl.pocketquest.pocketquest.utils

import android.content.Context
import android.graphics.Bitmap
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import java.util.*

object IconCache {

    private val cache = WeakHashMap<Bitmap, Icon>()

    fun get(context: Context, bitmap: Bitmap): Icon = cache[bitmap] ?: IconFactory
            .getInstance(context)
            .fromBitmap(bitmap)
            .also { cache[bitmap] = it }
}
