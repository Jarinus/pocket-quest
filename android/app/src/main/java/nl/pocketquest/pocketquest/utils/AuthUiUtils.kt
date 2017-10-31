package nl.hanze.distanceswimming.extensions.firebase

import android.content.Intent
import com.firebase.ui.auth.AuthUI
val AUTH_UI get() = AuthUI.getInstance()

inline fun AuthUI.buildSignInIntent(settings: AuthUI.SignInIntentBuilder.() -> Unit): Intent {
    val builder = createSignInIntentBuilder()
    settings(builder)
    return builder.build()
}

fun AuthUI.SignInIntentBuilder.setAvailableProviders(vararg providers: AuthUI.IdpConfig)
        = setAvailableProviders(providers.toList())

inline fun AuthUI.SignInIntentBuilder.provider(
        type: String,
        settings: AuthUI.IdpConfig.Builder.() -> Unit = {}
) = AuthUI.IdpConfig.Builder(type).also(settings).build()
