package nl.pocketquest.pocketquest.views.locationpermission

import nl.pocketquest.pocketquest.views.locationpermission.LocationPermissionContract.LocationPermissionView

class LocationPermissionPresenter(locationView: LocationPermissionView) : LocationPermissionContract.LocationPermissionPresenter(locationView) {
    override fun onDetach() {}

    override fun onPermissionGranted(granted: Boolean) {
        if (granted) {
            view.goToMainActivity()
        } else {
            view.setDisplayMessage("Can't start the application without location permissions")
        }
    }

    override fun onAttach() {
        view.setDisplayMessage("Please grant location permissions")
    }
}
