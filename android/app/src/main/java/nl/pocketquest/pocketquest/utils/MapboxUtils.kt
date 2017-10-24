package nl.pocketquest.pocketquest.utils

import android.location.Location
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import nl.pocketquest.pocketquest.SETTINGS.MAPBOXMAP.DEFAULT_CAMERA_ZOOM

fun Location.toLatLng() = LatLng(this)
operator fun LatLng.plus(other: LatLng) = LatLng(latitude + other.latitude, longitude + other.longitude)


fun MapboxMap.setCameraPosition(location: Location) {
    animateCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(), DEFAULT_CAMERA_ZOOM))
}

infix fun Number.latLong(longitude: Number) = LatLng(this.toDouble(), longitude.toDouble())

fun MapboxMap.addMarker(build: MarkerOptionBuilder.()->Unit) = addMarker(buildMarkerOptions(build))
fun buildMarkerOptions(build: MarkerOptionBuilder.()->Unit) = MarkerOptionBuilder.build(build)


class MarkerOptionBuilder{
    private var markerOptions = MarkerOptions()
    var icon : Icon
    get() = markerOptions.icon
    set(value) {markerOptions.icon = value}

    var position: LatLng
    get() = markerOptions.position
    set(value) {markerOptions.position = value}

    var snippet: String
    get() = markerOptions.snippet
    set(value) {markerOptions.snippet = value}

    var title: String
    get() = markerOptions.title
    set(value) {markerOptions.title = value}
    companion object {
        fun build(build: MarkerOptionBuilder.()->Unit)
                = MarkerOptionBuilder().also(build).markerOptions
    }
}

fun MapboxMapOptions.camera(cameraOptions: CameraPosition.Builder.()->Unit){
    camera(CameraPosition.Builder().also(cameraOptions).build())
}