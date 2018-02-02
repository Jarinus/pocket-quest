package nl.pocketquest.pocketquest.views.login

import android.app.Activity
import com.firebase.ui.auth.ErrorCodes

class LoginPresenter(view: LoginContract.LoginView) : LoginContract.LoginPresenter(view) {
    override fun onAttach() {
        view.startLoginAction()
    }

    override fun onDetach() {}

    override fun onLoginActionResult(resultCode: Int, errorCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            view.goToLocationPermissionActivity()
        } else view.displayToast(when (errorCode) {
            ErrorCodes.NO_NETWORK -> "No network"
            ErrorCodes.UNKNOWN_ERROR -> "Unexpected error"
            else -> "Unknown sign in response"
        })
    }
}
