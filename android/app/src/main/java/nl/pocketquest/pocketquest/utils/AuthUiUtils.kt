package nl.hanze.distanceswimming.extensions.firebase

import android.content.Intent
import com.firebase.ui.auth.AuthUI

inline fun AuthUI.buildSignInIntent(settings: AuthUI.SignInIntentBuilder.() -> Unit): Intent {
    val builder = createSignInIntentBuilder()
    settings(builder)
    return builder.build()
}

inline fun AuthUI.SignInIntentBuilder.provider(
        type: String,
        settings: AuthUI.IdpConfig.Builder.() -> Unit = {}
) = AuthUI.IdpConfig.Builder(type).also(settings).build()
