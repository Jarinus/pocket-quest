package nl.pocketquest.pocketquest.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import nl.hanze.distanceswimming.extensions.firebase.buildSignInIntent
import nl.hanze.distanceswimming.extensions.firebase.provider
import nl.pocketquest.pocketquest.MainActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


private const val RC_SIGN_IN = 1

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authUi = AuthUI.getInstance()
        val intent = authUi.buildSignInIntent {
            setAvailableProviders(listOf(
                    provider(AuthUI.GOOGLE_PROVIDER)
            ))
        }
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RC_SIGN_IN != requestCode) return
        val response = IdpResponse.fromResultIntent(data)
        when {
            resultCode == Activity.RESULT_OK -> startMainActivity()
            response == null -> toast("Response is null")
            response.errorCode == ErrorCodes.NO_NETWORK -> toast("No network")
            response.errorCode == ErrorCodes.UNKNOWN_ERROR -> toast("Unexpected error")
            else -> toast("Unknown sign in response")
        }
    }

    private inline fun startMainActivity() {
        startActivity<MainActivity>()
        finish()
    }
}
