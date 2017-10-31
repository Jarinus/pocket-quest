package nl.pocketquest.pocketquest.game.entities

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream

/**
 * Created by Laurens on 30-10-2017.
 */
@GlideModule
open class GlideModule : AppGlideModule() {

    override fun registerComponents(context: Context?, glide: Glide?, registry: Registry?) {
        super.registerComponents(context, glide, registry)
        registry?.append(StorageReference::class.java, InputStream::class.java, FirebaseImageLoader.Factory())
    }
}
