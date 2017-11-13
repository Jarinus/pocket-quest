package nl.pocketquest.pocketquest.views.map

import android.location.Location
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import com.uchuhimo.collections.BiMap
import com.uchuhimo.collections.mutableBiMapOf
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.SETTINGS
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.FirebaseImageResolver
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.location.LocationEngineWrapper
import nl.pocketquest.pocketquest.mvp.BaseActivity
import nl.pocketquest.pocketquest.utils.*
import org.jetbrains.anko.info

private const val MAPBOX_TAG = "com.mapbox.map"

class MapActivity : BaseActivity(), MapContract.MapView {
    private var map: MapboxMap? = null
    private val presenter: MapContract.MapPresenter = MapPresenter(this)
    private val locationEngineWrapper = LocationEngineWrapper(this, presenter::onLocationChanged)
    private val gameObjectsMarkerBiMap = mutableBiMapOf<IGameObject, Marker>()

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
        locationEngineWrapper.lastLocation?.apply(presenter::onLocationChanged)
        map?.setOnMarkerClickListener {
            gameObjectsMarkerBiMap
                    .inverse[it]
                    ?.also(presenter::onGameObjectClicked)
            true
        }
    }

    override fun focusMapCenterOn(location: Location) {
        runOnUiThread {
            map?.setCameraPosition(location)
        }
    }

    override fun addGameObject(gameObject: IGameObject) {
        runOnUiThread {
            val marker = map?.addMarker {
                icon = IconCache.get(this@MapActivity, gameObject.image)
                position = gameObject.location
            } ?: return@runOnUiThread
            gameObject.onChange {
                runOnUiThread {
                    marker.icon = IconCache.get(this@MapActivity, it.image)
                    marker.position = it.location
                }
            }
            gameObjectsMarkerBiMap[gameObject] = marker
        }
    }

    override fun getImageResolver() = object : ImageResolver {
        suspend override fun resolveImage(imageID: String) =
                FirebaseImageResolver.resolveImage(this@MapActivity, imageID)
    }

    override fun removeGameObject(gameObject: IGameObject) {
        gameObjectsMarkerBiMap[gameObject]
                ?.also { map?.removeMarker(it) }
        gameObjectsMarkerBiMap -= gameObject
    }
}