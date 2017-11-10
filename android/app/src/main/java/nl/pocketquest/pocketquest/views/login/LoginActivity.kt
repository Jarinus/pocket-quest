package nl.pocketquest.pocketquest.views.login

import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import nl.hanze.distanceswimming.extensions.firebase.AUTH_UI
import nl.hanze.distanceswimming.extensions.firebase.buildSignInIntent
import nl.hanze.distanceswimming.extensions.firebase.provider
import nl.hanze.distanceswimming.extensions.firebase.setAvailableProviders
import nl.pocketquest.pocketquest.mvp.BaseActivity
import nl.pocketquest.pocketquest.views.map.MapActivity
import org.jetbrains.anko.startActivity

private const val RC_SIGN_IN = 1

class LoginActivity : BaseActivity(), LoginContract.LoginView {
    private val presenter = LoginPresenter(this)

    override fun goToMainActivity() {
        startActivity<MapActivity>()
        finish()
    }

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
        val errorCode = response?.errorCode ?: return
        presenter.handleAuthenticationResult(resultCode, errorCode)
    }
}
