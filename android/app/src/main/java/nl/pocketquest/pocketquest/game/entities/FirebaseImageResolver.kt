package nl.pocketquest.pocketquest.game.entities

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import nl.pocketquest.pocketquest.utils.STORAGE
import nl.pocketquest.pocketquest.utils.toBitmap
import org.jetbrains.anko.AnkoLogger

interface ImageResolver {
    suspend fun resolveImage(imageID: String): Bitmap
}

object FirebaseImageResolver : AnkoLogger {

    suspend fun resolveImage(context: Context, iconName: String) =
            GlideApp.with(context)
                    .load(STORAGE.getReference(iconName))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit()
                    .get()
                    .toBitmap()
}

fun ImageView.load(context: Context, iconName: String) =
        GlideApp.with(context)
                .load(STORAGE.getReference(iconName))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerInside()
                .into(this)
