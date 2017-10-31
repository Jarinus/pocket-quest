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
        if (requestCode === RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode === Activity.RESULT_OK) {
                startActivity<MainActivity>()
                finish()
                return
            }else

            // Sign in failed
            if (response == null) {
                toast("Response is null")
                return
            }else

            if (response.errorCode == ErrorCodes.NO_NETWORK) {
                toast("No network")
                return
            }else

            if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                toast("Unexpected error")
                return
            }

            toast("Unknown sign in response")
        }
    }
}
