package nl.pocketquest.pocketquest.views.main.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.uchuhimo.collections.mutableBiMapOf
import kotlinx.android.synthetic.main.activity_main.view.*
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.FirebaseImageResolver
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.location.LocationEngineWrapper
import nl.pocketquest.pocketquest.mvp.BaseFragment
import nl.pocketquest.pocketquest.utils.IconCache
import nl.pocketquest.pocketquest.utils.addMarker
import nl.pocketquest.pocketquest.utils.setCameraPosition
import org.jetbrains.anko.info
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.ctx

class MapFragment : BaseFragment(), MapContract.MapView {
    private var map: MapboxMap? = null
    private val presenter: MapContract.MapPresenter = MapPresenter(this)
    private lateinit var locationEngineWrapper: LocationEngineWrapper
    private val gameObjectsMarkerBiMap = mutableBiMapOf<IGameObject, Marker>()
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "Starting onCreate" }
        locationEngineWrapper = LocationEngineWrapper(ctx, presenter::onLocationChanged)
        lifecycle.addObserver(locationEngineWrapper)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Mapbox.getInstance(ctx, getString(R.string.mapbox_key))
        val view = inflater.inflate(R.layout.activity_main, container, false)
        val mapView = view.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this::initializeMap)
        this.mapView = mapView
        return view
    }

    private fun initializeMap(mapboxMap: MapboxMap) {
        map = mapboxMap
        presenter.onMapReady()
        locationEngineWrapper.lastLocation?.apply(presenter::onLocationChanged)
        map?.setOnMarkerClickListener {
            info { "little tap on a game object" }
            gameObjectsMarkerBiMap
                    .inverse[it]
                    ?.let(presenter::onGameObjectClicked)
                    ?: false
        }
    }

    override fun focusMapCenterOn(location: Location) {
        ctx.runOnUiThread {
            map?.setCameraPosition(location)
        }
    }

    override fun addGameObject(gameObject: IGameObject) {
        ctx.runOnUiThread {
            val marker = map?.addMarker {
                icon = IconCache.get(ctx, gameObject.image)
                position = gameObject.location
            } ?: return@runOnUiThread
            gameObject.onChange {
                runOnUiThread {
                    marker.icon = IconCache.get(ctx, it.image)
                    marker.position = it.location
                }
            }
            gameObjectsMarkerBiMap[gameObject] = marker
        }
    }

    override fun getImageResolver() = object : ImageResolver {
        suspend override fun resolveImage(imageID: String) =
                FirebaseImageResolver.resolveImage(ctx, imageID)
    }

    override fun removeGameObject(gameObject: IGameObject) {
        gameObjectsMarkerBiMap[gameObject]
                ?.also { map?.removeMarker(it) }
        gameObjectsMarkerBiMap -= gameObject
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outstate: Bundle) {
        super.onSaveInstanceState(outstate)
        mapView?.onSaveInstanceState(outstate)
    }
}
