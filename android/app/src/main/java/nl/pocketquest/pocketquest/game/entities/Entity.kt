package nl.pocketquest.pocketquest.game.entities

import android.content.Context
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.utils.toBitmap
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.info

class Entity(private val iconName: String) : AnkoLogger {
    fun resolveImage(context: Context) {
        val ref = context.asReference()
        async(CommonPool) {
            GlideApp.with(ref.invoke())
                    .load(FirebaseStorage.getInstance().getReference(iconName))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit()
                    .get()
                    .toBitmap()
                    .also {
                        info { "Loaded bitmap width ${it.width} height ${it.height}" }
                    }
        }
    }
}
