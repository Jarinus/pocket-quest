package nl.pocketquest.pocketquest.views.map

import android.location.Location
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.SETTINGS
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.location.LocationEngineWrapper
import nl.pocketquest.pocketquest.mvp.BaseActivity
import nl.pocketquest.pocketquest.utils.*
import org.jetbrains.anko.info

private const val MAPBOX_TAG = "com.mapbox.map"

class MainActivity : BaseActivity(), MapContract.MapView {

    private var map: MapboxMap? = null
    private val presenter: MapContract.MapPresenter = MapPresenter(this)
    private val locationEngineWrapper = LocationEngineWrapper(this, presenter::onLocationChanged)
    private val iconCache = IconCache(this)
    private val gameObjectsToMarker = mutableMapOf<GameObject, Marker>()
    private val markerToGameObjects = mutableMapOf<Marker, GameObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "Starting onCreate" }
        lifecycle.addObserver(locationEngineWrapper)
        setContentView(R.layout.activity_main)
        Mapbox.getInstance(this, getString(R.string.mapbox_key))
        createOrLoadMapView(savedInstanceState).getMapAsync(this::initializeMap)
    }

    private fun createOrLoadMapView(savedInstanceState: Bundle?): SupportMapFragment {
        savedInstanceState ?: (supportFragmentManager.findFragmentByTag(MAPBOX_TAG) as? SupportMapFragment)?.also { return it }
        val mapFragment = SupportMapFragment.newInstance(createMapboxOptions())
        supportFragmentManager.doTransaction {
            replace(R.id.mapFragment, mapFragment, MAPBOX_TAG)
        }
        return mapFragment
    }

    private fun createMapboxOptions() = buildMapboxOptions {
        styleUrl = getString(R.string.mapbox_custom_style)
        zoomPreference = SETTINGS.MAPBOX_MAP.CAMERA_ZOOM
        enabledgestures {
            all = false
        }
        cameraPosition {
            zoom(SETTINGS.MAPBOX_MAP.CAMERA_ZOOM)
            tilt(SETTINGS.MAPBOX_MAP.CAMERA_TILT)
        }
    }

    private fun initializeMap(mapboxMap: MapboxMap) {
        map = mapboxMap
        presenter.onMapReady()
        locationEngineWrapper.getLastLocation()?.apply(presenter::onLocationChanged)
        map?.setOnMarkerClickListener {
            markerToGameObjects[it]?.also(presenter::onGameObjectClicked)
            true
        }
    }

    override fun focusMapCenterOn(location: Location) {
        runOnUiThread {
            map?.setCameraPosition(location)
        }
    }

    override fun addGameObject(gameObject: GameObject) {
        runOnUiThread {
            val marker = map?.addMarker {
                icon = iconCache.get(gameObject.image)
                position = gameObject.location
            } ?: return@runOnUiThread
            gameObject.onChange {
                runOnUiThread {
                    marker.icon = iconCache.get(it.image)
                    marker.position = it.location

                }
            }
            gameObjectsToMarker[gameObject] = marker
            markerToGameObjects[marker] = gameObject
        }
    }

    override fun removeGameObject(gameObject: GameObject) {
        gameObjectsToMarker[gameObject]?.also { map?.removeMarker(it) }
        gameObjectsToMarker -= gameObject
    }
}
