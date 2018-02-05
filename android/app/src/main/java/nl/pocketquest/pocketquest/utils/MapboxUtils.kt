package nl.pocketquest.pocketquest.utils

import android.location.Location
import com.firebase.geofire.GeoLocation
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import nl.pocketquest.pocketquest.SETTINGS.MAPBOX_MAP.CAMERA_ZOOM

fun Location.toLatLng() = LatLng(this)
fun Location.toGoogleLatLng() = com.google.android.gms.maps.model.LatLng(latitude, longitude)
fun LatLng.toGeoLocation() = GeoLocation(latitude, longitude)
fun LatLng.toGoogleLatLng() = com.google.android.gms.maps.model.LatLng(latitude, longitude)
fun GeoLocation.toLatLng() = LatLng(latitude, longitude)
operator fun LatLng.plus(other: LatLng) = LatLng(latitude + other.latitude, longitude + other.longitude)

fun MapboxMap.setCameraPosition(location: Location) {
    animateCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(), CAMERA_ZOOM))
}

infix fun Number.latLong(longitude: Number) = LatLng(this.toDouble(), longitude.toDouble())

fun MapboxMap.addMarker(build: MarkerOptionBuilder.() -> Unit) = addMarker(buildMarkerOptions(build))
fun buildMarkerOptions(build: MarkerOptionBuilder.() -> Unit) = MarkerOptionBuilder.build(build)

class MarkerOptionBuilder {
    private var markerOptions = MarkerOptions()
    var icon: Icon
        get() = markerOptions.icon
        set(value) {
            markerOptions.icon = value
        }

    var position: LatLng
        get() = markerOptions.position
        set(value) {
            markerOptions.position = value
        }

    var snippet: String
        get() = markerOptions.snippet
        set(value) {
            markerOptions.snippet = value
        }

    var title: String
        get() = markerOptions.title
        set(value) {
            markerOptions.title = value
        }

    companion object {
        fun build(build: MarkerOptionBuilder.() -> Unit)
                = MarkerOptionBuilder().also(build).markerOptions
    }
}

fun buildMapboxOptions(init: MapBoxOptionsDSL.() -> Unit) = MapBoxOptionsDSL.build(init)

class MapBoxOptionsDSL(private val mapboxMapOptions: MapboxMapOptions) {
    var enabledgestures = Gestures()

    companion object {
        fun build(init: MapBoxOptionsDSL.() -> Unit)
                = MapBoxOptionsDSL(MapboxMapOptions()).also(init).build()
    }

    private fun build() = mapboxMapOptions.apply {
        logoEnabled(false)
        attributionEnabled(false)
    }

    fun cameraPosition(cameraOptions: CameraPosition.Builder.() -> Unit) {
        val cameraPosition = CameraPosition.Builder().also(cameraOptions).build()
        mapboxMapOptions.camera(cameraPosition)
    }

    var zoomPreference: Double
        get() = mapboxMapOptions.minZoomPreference
        set(value) {
            mapboxMapOptions.minZoomPreference(value)
            mapboxMapOptions.maxZoomPreference(value)
        }

    var styleUrl
        get() = mapboxMapOptions.style
        set(value) {
            mapboxMapOptions.styleUrl(value)
        }

    inner class Gestures {
        operator fun invoke(apply: Gestures.() -> Unit) = this.apply(apply)
        var scroll: Boolean
            get() = mapboxMapOptions.scrollGesturesEnabled
            set(value) {
                mapboxMapOptions.scrollGesturesEnabled(value)
            }

        var zoom: Boolean
            get() = mapboxMapOptions.zoomGesturesEnabled
            set(value) {
                mapboxMapOptions.zoomGesturesEnabled(value)
            }

        var rotate: Boolean
            get() = mapboxMapOptions.rotateGesturesEnabled
            set(value) {
                mapboxMapOptions.rotateGesturesEnabled(value)
            }

        var tilt: Boolean
            get() = mapboxMapOptions.tiltGesturesEnabled
            set(value) {
                mapboxMapOptions.tiltGesturesEnabled(value)
            }

        var doubleTap: Boolean
            get() = mapboxMapOptions.doubleTapGesturesEnabled
            set(value) {
                mapboxMapOptions.doubleTapGesturesEnabled(value)
            }

        var all: Boolean
            set(value) {
                scroll = false
                zoom = false
                rotate = false
                tilt = false
                doubleTap = false
            }
            get() = scroll && zoom && rotate && tilt && doubleTap
    }
}
