package nl.pocketquest.pocketquest.views.login

import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView

/**
 * Created by Laurens on 6-11-2017.
 */
class LoginContract {

    interface LoginView : BaseView {
        fun goToLocationPermissionActivity()
    }

    abstract class LoginPresenter(loginView: LoginView) : BasePresenter<LoginView>(loginView) {
        abstract fun handleAuthenticationResult(resultCode: Int, errorCode: Int)
    }
}
