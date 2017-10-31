package nl.pocketquest.pocketquest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import nl.hanze.distanceswimming.extensions.firebase.AUTH_UI
import nl.hanze.distanceswimming.extensions.firebase.setAvailableProviders
import nl.hanze.distanceswimming.extensions.firebase.buildSignInIntent
import nl.hanze.distanceswimming.extensions.firebase.provider
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


private const val RC_SIGN_IN = 1

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = AUTH_UI.buildSignInIntent {
            setAvailableProviders(
                    provider(AuthUI.GOOGLE_PROVIDER)
            )
        }
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RC_SIGN_IN != requestCode) return
        val response = IdpResponse.fromResultIntent(data)
        if (resultCode == Activity.RESULT_OK) {
            startActivity<MainActivity>()
            finish()
        } else toast(when {
            response == null -> "Response is null"
            response.errorCode == ErrorCodes.NO_NETWORK -> "No network"
            response.errorCode == ErrorCodes.UNKNOWN_ERROR -> "Unexpected error"
            else -> "Unknown sign in response"
        })
    }
}
