package nl.pocketquest.pocketquest.views.login

import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import nl.pocketquest.pocketquest.utils.AUTH_UI
import nl.pocketquest.pocketquest.utils.buildSignInIntent
import nl.pocketquest.pocketquest.utils.setAvailableProviders
import nl.pocketquest.pocketquest.views.BaseActivity
import nl.pocketquest.pocketquest.views.locationpermission.LocationPermissionActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

private const val RC_SIGN_IN = 1

class LoginActivity : BaseActivity(), LoginContract.LoginView {
    override fun displayToast(message: String) { toast(message) }

    private val presenter = LoginPresenter(this)

    override fun goToLocationPermissionActivity() {
        startActivity<LocationPermissionActivity>()
        presenter.onDetach()
        finish()
    }

    override fun startLoginAction(){
        val intent = AUTH_UI.buildSignInIntent {
            setAvailableProviders(
                    AuthUI.IdpConfig.GoogleBuilder().build()
            )
        }
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onAttach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RC_SIGN_IN != requestCode) return
        val response = IdpResponse.fromResultIntent(data)
        val errorCode = response?.errorCode ?: return
        presenter.onLoginActionResult(resultCode, errorCode)
    }
}
