package nl.pocketquest.pocketquest

import com.google.android.gms.location.LocationRequest

object SETTINGS {
    object LOCATION_ENGINE {
        const val DEFAULT_FASTEST_INTERVAL = 2000L
        const val DEFAULT_INTERVAL = 10_000L
        const val ACCURACY_MODE = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    object MAPBOX_MAP {
        const val CAMERA_ZOOM = 18.0
        const val CAMERA_TILT = 50.25
    }
}
