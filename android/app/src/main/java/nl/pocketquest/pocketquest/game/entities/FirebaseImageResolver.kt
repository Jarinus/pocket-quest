package nl.pocketquest.pocketquest.game.entities

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.utils.STORAGE
import nl.pocketquest.pocketquest.utils.toBitmap
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.asReference

object FirebaseImageResolver : AnkoLogger {

    fun resolveImage(context: Context, iconName: String, onSuccess: (Bitmap) -> Unit) {
        val ref = context.asReference()
        async(CommonPool) {
            GlideApp.with(context)
                    .load(STORAGE.getReference(iconName))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit()
                    .get()
                    .toBitmap()
                    .also(onSuccess)
        }
    }
}
