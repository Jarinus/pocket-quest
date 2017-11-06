package nl.pocketquest.pocketquest.views.login

import android.app.Activity
import com.firebase.ui.auth.ErrorCodes

/**
 * Created by Laurens on 6-11-2017.
 */
class LoginPresenter(view: LoginContract.LoginView) : LoginContract.LoginPresenter(view) {

    override fun handleAuthenticationResult(resultCode: Int, errorCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            view.goToMainActivity()
        } else view.displayToast(when {
            errorCode == ErrorCodes.NO_NETWORK -> "No network"
            errorCode == ErrorCodes.UNKNOWN_ERROR -> "Unexpected error"
            else -> "Unknown sign in response"
        })
    }
}
