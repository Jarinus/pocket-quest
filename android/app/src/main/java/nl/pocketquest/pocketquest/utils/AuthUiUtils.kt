package nl.pocketquest.pocketquest.utils

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
