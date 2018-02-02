package nl.pocketquest.pocketquest.views.locationpermission

import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView


class LocationPermissionContract {

    interface LocationPermissionView : BaseView {
        fun goToMainActivity()
        fun setDisplayMessage(message: String)
    }

    abstract class LocationPermissionPresenter(locationPermissionView: LocationPermissionView)
        : BasePresenter<LocationPermissionView>(locationPermissionView) {
        abstract fun onPermissionGranted(granted: Boolean)
    }
}
