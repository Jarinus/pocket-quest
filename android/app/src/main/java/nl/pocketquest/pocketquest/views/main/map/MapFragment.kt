package nl.pocketquest.pocketquest.views.main.map

import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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

    private lateinit var map: GoogleMap

    private val presenter: MapContract.MapPresenter = MapPresenter(this)
    private lateinit var locationEngineWrapper: LocationEngineWrapper
    private val gameObjectsMarkerBiMap = mutableBiMapOf<IGameObject, Marker>()
    private var mapView: MapView? = null

    override fun decodeResource(resourceID: Int): Bitmap = ctx.decodeResource(resourceID)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "Starting onCreate" }
        locationEngineWrapper = LocationEngineWrapper(ctx, presenter::onLocationChanged)
        lifecycle.addObserver(locationEngineWrapper)
        mapView?.onCreate(savedInstanceState)
        fragmentManager!!.beginTransaction().also {
            it.replace(R.id.mapOverlay, MapOverlayFragment())
        }.commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapView = view.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this::initializeMap)
        this.mapView = mapView
        return view
    }

    private fun initializeMap(googleMap: GoogleMap) {
        map = googleMap
        presenter.onMapReady()
        locationEngineWrapper.lastLocation?.apply(presenter::onLocationChanged)
        map.setOnMarkerClickListener {
            info { "little tap on a game object" }
            gameObjectsMarkerBiMap
                    .inverse[it]
                    ?.let(presenter::onGameObjectClicked)
                    ?: false
        }
    }

    override fun focusMapCenterOn(location: Location) {
        ctx.runOnUiThread {
            map.moveCamera(CameraUpdateFactory.newLatLng(location.toGoogleLatLng()))
        }
    }

    override fun addGameObject(gameObject: IGameObject) {
        ctx.runOnUiThread {
            val marker = map.addMarker(
                    MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(gameObject.image))
                            .position(gameObject.location.toGoogleLatLng())
            )
            gameObject.onChange {
                runOnUiThread {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(it.image))
                    marker.position = it.location.toGoogleLatLng()
                }
            }
            gameObjectsMarkerBiMap[gameObject] = marker
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
