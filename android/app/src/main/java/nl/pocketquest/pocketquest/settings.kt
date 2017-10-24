package nl.pocketquest.pocketquest

import android.content.Context
import com.mapbox.services.android.location.LostLocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEnginePriority

object SETTINGS{
    object LOCATION_ENGINE{
        const val DEFAULT_FASTEST_INTERVAL = 500
        const val DEFAULT_INTERVAL = 1000
        const val ACCURACY_MODE = LocationEnginePriority.HIGH_ACCURACY
        val LOCATION_PROVIDER : (Context)-> LocationEngine = LostLocationEngine::getLocationEngine
    }
    object MAPBOXMAP{
        const val DEFAULT_CAMERA_ZOOM = 18.0
    }
}