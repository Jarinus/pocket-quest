package nl.pocketquest.pocketquest.utils

import android.content.Context
import android.graphics.Bitmap
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import java.util.*

/**
 * Created by Laurens on 6-11-2017.
 */
class IconCache(private val context: Context) {

    private val cache = WeakHashMap<Bitmap, Icon>()

    fun get(bitmap: Bitmap): Icon {
        var icon = cache[bitmap] ?: IconFactory.getInstance(context).fromBitmap(bitmap)
        cache[bitmap] = icon
        return icon
    }
}
