package nl.pocketquest.pocketquest.utils

import android.location.Location
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng

fun Location.toGoogleLatLng() = com.google.android.gms.maps.model.LatLng(latitude, longitude)
fun LatLng.toGeoLocation() = GeoLocation(latitude, longitude)
fun LatLng.toGoogleLatLng() = com.google.android.gms.maps.model.LatLng(latitude, longitude)
fun GeoLocation.toLatLng() = LatLng(latitude, longitude)
operator fun LatLng.plus(other: LatLng) = LatLng(latitude + other.latitude, longitude + other.longitude)

infix fun Number.latLong(longitude: Number) = LatLng(this.toDouble(), longitude.toDouble())
