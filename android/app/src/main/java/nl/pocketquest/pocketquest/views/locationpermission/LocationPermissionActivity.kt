package nl.pocketquest.pocketquest.views.locationpermission

import android.os.Bundle
import android.widget.TextView
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import nl.pocketquest.pocketquest.mvp.BaseActivity
import nl.pocketquest.pocketquest.views.locationpermission.LocationPermissionContract.LocationPermissionView
import nl.pocketquest.pocketquest.views.main.MainActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class LocationPermissionActivity : BaseActivity(), PermissionsListener, LocationPermissionView {
    private var permissionsManager: PermissionsManager? = null
    private var messageText: TextView? = null
    val presenter = LocationPermissionPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            messageText = textView("")
        }
        presenter.onAttach()
        requestLocationPermission()
    }

    @SuppressWarnings("MissingPermission")
    private fun requestLocationPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = PermissionsManager(this).also {
                it.requestLocationPermissions(this)
            }
        } else {
            presenter.onPermissionGranted(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionResult(granted: Boolean) {
        presenter.onPermissionGranted(granted)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit

    override fun goToMainActivity() {
        startActivity<MainActivity>()
        finish()
    }

    override fun setDisplayMessage(message: String) {
        messageText?.text = message
    }
}
