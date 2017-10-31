package nl.pocketquest.pocketquest

import android.content.Context
import com.mapbox.services.android.location.LostLocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEnginePriority

object SETTINGS {
    object LOCATION_ENGINE {
        const val DEFAULT_FASTEST_INTERVAL = 1000
        const val DEFAULT_INTERVAL = 2000
        const val ACCURACY_MODE = LocationEnginePriority.HIGH_ACCURACY
        val LOCATION_PROVIDER: (Context) -> LocationEngine = LostLocationEngine::getLocationEngine
    }

    object MAPBOX_MAP {
        const val CAMERA_ZOOM = 18.0
        const val CAMERA_TILT = 50.25
    }
}
