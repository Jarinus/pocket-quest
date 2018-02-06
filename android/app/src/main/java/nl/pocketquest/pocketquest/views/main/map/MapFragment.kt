package nl.pocketquest.pocketquest.views.main.map

import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uchuhimo.collections.mutableBiMapOf
import kotlinx.android.synthetic.main.fragment_map.view.*
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.FirebaseImageResolver
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.location.LocationEngineWrapper
import nl.pocketquest.pocketquest.utils.decodeResource
import nl.pocketquest.pocketquest.utils.toGoogleLatLng
import nl.pocketquest.pocketquest.views.BaseFragment
import nl.pocketquest.pocketquest.views.main.map.overlay.MapOverlayFragment
import org.jetbrains.anko.info
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.ctx

class MapFragment : BaseFragment(), MapContract.MapView {

    private var map: GoogleMap? = null

    private val presenter: MapContract.MapPresenter = MapPresenter(this)
    private lateinit var locationEngineWrapper: LocationEngineWrapper
    private val gameObjectsMarkerBiMap = mutableBiMapOf<IGameObject, Marker>()
    private val gameObjects = mutableListOf<IGameObject>()
    private var mapView: MapView? = null

    override fun decodeResource(resourceID: Int): Bitmap = ctx.decodeResource(resourceID)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "Starting onCreate" }
        locationEngineWrapper = LocationEngineWrapper(ctx, presenter::onLocationChanged)
        lifecycle.addObserver(locationEngineWrapper)
        mapView?.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        info { "onCreateView" }
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapView = view.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this::initializeMap)
        this.mapView = mapView
        fragmentManager!!.beginTransaction().also {
            it.replace(R.id.mapOverlay, MapOverlayFragment())
        }.commit()
        return view
    }

    private fun initializeMap(googleMap: GoogleMap) {
        info { "Initialize map" }
        map?.clear()
        map = googleMap
        gameObjects.forEach(this::addGameObjectToMapView)
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))
        presenter.onMapReady()
        locationEngineWrapper.loadLastLocation()
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
            map?.moveCamera(CameraUpdateFactory.newLatLng(location.toGoogleLatLng()))
        }
    }

    override fun addGameObject(gameObject: IGameObject) {
        gameObjects.add(gameObject)
        addGameObjectToMapView(gameObject)
    }

    private fun addGameObjectToMapView(gameObject: IGameObject) {
        ctx.runOnUiThread {
            val marker = map?.addMarker(
                    MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(gameObject.image))
                            .position(gameObject.location.toGoogleLatLng())
            ) ?: return@runOnUiThread
            gameObjectsMarkerBiMap[gameObject] = marker
            gameObject.onChange {
                gameObjectsMarkerBiMap[gameObject]?.also { foundMarker ->
                    runOnUiThread {
                        foundMarker.setIcon(BitmapDescriptorFactory.fromBitmap(it.image))
                        foundMarker.position = it.location.toGoogleLatLng()
                    }
                }
            }
        }
    }

    override fun getImageResolver() = object : ImageResolver {
        override suspend fun resolveImage(imageID: String) =
                FirebaseImageResolver.resolveImage(ctx, imageID)
    }

    override fun removeGameObject(gameObject: IGameObject) {
        gameObjectsMarkerBiMap[gameObject]
                ?.also { it.remove() }
        gameObjectsMarkerBiMap -= gameObject
    }

    override fun onStart() {
        info { "onStart" }
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        info { "onResume" }
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        info { "onPause" }
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        info { "onStop" }
        super.onStop()
        gameObjectsMarkerBiMap.clear()
        mapView?.onStop()
    }

    private fun cleanUp() {
        gameObjectsMarkerBiMap.keys.forEach {
            it.close()
        }
    }

    override fun onLowMemory() {
        info { "onLowMemory" }
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        info { "onDestroy" }
        super.onDestroy()
        cleanUp()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outstate: Bundle) {
        info { "onSaveInstanceState" }
        super.onSaveInstanceState(outstate)
        mapView?.onSaveInstanceState(outstate)
    }
}
