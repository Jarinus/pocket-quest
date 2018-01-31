package nl.pocketquest.pocketquest.game.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.utils.STORAGE
import nl.pocketquest.pocketquest.utils.toBitmap
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.wtf

interface ImageResolver {
    suspend fun resolveImage(imageID: String): Bitmap
}

object FirebaseImageResolver : AnkoLogger {

    suspend fun resolveImage(context: Context, iconName: String) =
            makeImgRequest(context, iconName)
                .submit()
                .get()
                .toBitmap()

    suspend fun makeImgRequest(context: Context, iconName: String): GlideRequest<Drawable> {
        try {
            check(iconName.isNotBlank())
        } catch (e: Exception){
            wtf("iconName cannot be blank", e)
        }
        return GlideApp.with(context)
                .load(STORAGE.getReference("images/$iconName"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
    }
}

fun ImageView.load(context: Context, iconName: String) = async(CommonPool){
    FirebaseImageResolver.makeImgRequest(context,iconName)
            .centerInside()
            .into(this@load)
}
