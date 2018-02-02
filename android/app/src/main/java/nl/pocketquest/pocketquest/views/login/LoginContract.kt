package nl.pocketquest.pocketquest.views.login

import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView

class LoginContract {

    interface LoginView : BaseView {
        fun goToLocationPermissionActivity()
        fun startLoginAction()
    }

    abstract class LoginPresenter(loginView: LoginView) : BasePresenter<LoginView>(loginView) {
        abstract fun onLoginActionResult(resultCode: Int, errorCode: Int)
    }
}
