package nl.pocketquest.pocketquest

import android.location.Location
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

/**
 * Created by Laurens on 18-10-2017.
 */
fun Location.toLatLng() = LatLng(this)

operator fun LatLng.plus(other: LatLng) = LatLng(latitude + other.latitude, longitude + other.longitude)


fun MapboxMap.setCameraPosition(location: Location) {
    animateCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(), MainActivity.DEFAULT_CAMERA_ZOOM))
}