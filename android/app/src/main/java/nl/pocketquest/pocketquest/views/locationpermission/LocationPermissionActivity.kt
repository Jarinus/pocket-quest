package nl.pocketquest.pocketquest.views.locationpermission

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import nl.pocketquest.pocketquest.views.map.MapActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class LocationPermissionActivity : AppCompatActivity(), PermissionsListener {
    private var permissionsManager: PermissionsManager? = null
    private var messageText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            messageText = textView("Please grant location permission")
        }
        requestLocationPermission()
    }

    @SuppressWarnings("MissingPermission")
    private fun requestLocationPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = PermissionsManager(this).also {
                it.requestLocationPermissions(this)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionResult(granted: Boolean) {
        if (!granted) {
            messageText?.setText("Can't start app without location permission. Restart and grant access.")
            finish()
        } else startActivity<MapActivity>()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit
}
