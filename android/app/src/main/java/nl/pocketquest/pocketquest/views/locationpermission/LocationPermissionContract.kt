package nl.pocketquest.pocketquest.views.locationpermission

import nl.pocketquest.pocketquest.views.BasePresenter
import nl.pocketquest.pocketquest.views.BaseView


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
