package nl.pocketquest.pocketquest.views.login

import nl.pocketquest.pocketquest.views.BasePresenter
import nl.pocketquest.pocketquest.views.BaseView

class LoginContract {

    interface LoginView : BaseView {
        fun goToLocationPermissionActivity()
        fun startLoginAction()
        fun displayToast(message: String)
    }

    abstract class LoginPresenter(loginView: LoginView) : BasePresenter<LoginView>(loginView) {
        abstract fun onLoginActionResult(resultCode: Int, errorCode: Int)
    }
}
