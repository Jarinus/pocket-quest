package nl.pocketquest.pocketquest.sprites

import android.content.Context
import android.graphics.Bitmap
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import org.jetbrains.anko.doAsync

/**
 * Created by Laurens on 20-10-2017.
 */
class SpriteSheet(val context: Context, val marker: Marker, frames: List<Bitmap>, val animationDuration: Int) {

    private var index = 0
    private val frames: List<Icon>

    init {
        this.frames = frames.map { IconFactory.getInstance(context).fromBitmap(it) }.toList()
    }

    fun start() {
        doAsync {
            while (true) {
                marker.icon = frames[index]
                index = (index + 1) % frames.size
                Thread.sleep(animationDuration.toLong())
            }
        }
    }
}