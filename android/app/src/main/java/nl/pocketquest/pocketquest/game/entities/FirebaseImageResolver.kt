package nl.pocketquest.pocketquest.game.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
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
            createImageRequest(context, iconName)
                    .submit()
                    .get()
                    .toBitmap()
}

private fun createImageRequest(context: Context, iconName: String): GlideRequest<Drawable> {
    try{
        require(iconName.isNotBlank())
    } catch (e: Exception){
        Log.wtf("iconName cannot be blank", e)
    }
    return GlideApp.with(context)
            .load(STORAGE.getReference("images/$iconName"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
}

fun ImageView.load(context: Context, iconName: String) = createImageRequest(context, iconName)
                .centerInside()
                .into(this)
