package nl.pocketquest.pocketquest.views.main.map

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.Clickable
import nl.pocketquest.pocketquest.game.FirebaseGameObjectInput
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.construction.GameObjectAcceptor
import nl.pocketquest.pocketquest.sprites.GameObjectAnimator
import nl.pocketquest.pocketquest.sprites.SpriteSheetCreator
import nl.pocketquest.pocketquest.sprites.padded
import nl.pocketquest.pocketquest.utils.latLong
import nl.pocketquest.pocketquest.utils.toLatLng
import nl.pocketquest.pocketquest.utils.xy
import org.jetbrains.anko.info

const val ANIMATION_DURATION = 42

class MapPresenter(mapView: MapContract.MapView) : MapContract.MapPresenter(mapView), GameObjectAcceptor {
    override fun onAttach() = Unit

    override fun onDetach() = Unit

    private var cachedLocation: Location? = null
    private var ready = false
    private var player: IGameObject? = null
    private val keyToGameObject = mutableMapOf<String, IGameObject>()
    private var gameObjectInput: FirebaseGameObjectInput? = null

    private fun createPlayerMarker(): IGameObject {
        val frames = SpriteSheetCreator(view.decodeResource(R.drawable.santasprite), 4 xy 4)
                .frames
                .map { it.padded(1.1 xy 1.7) }

        return GameObject(0 latLong 0, frames.first()).also {
            GameObjectAnimator(it, frames, ANIMATION_DURATION).start()
        }
    }

    override fun onGameObjectClicked(gameObject: IGameObject): Boolean {
        if (gameObject === player) {
            return false
        }
        info { "Game object was clicked" }
        (gameObject as? Clickable<*>)?.clicked()
        return true
    }

    override fun onMapReady() {
        ready = true
        cachedLocation?.also { setNewLocation(it) }
        player = createPlayerMarker()
                .also { view.addGameObject(it) }
    }

    override fun onLocationChanged(location: Location) {
        if (ready) {
            setNewLocation(location)
        } else cachedLocation = location
    }

    private fun setNewLocation(location: Location) {
        player?.location = location.toLatLng()
        view.focusMapCenterOn(location)
        createOrUpdateGeoQuery(location)
    }

    private fun createOrUpdateGeoQuery(location: Location) {
        if (gameObjectInput == null) {
            gameObjectInput = FirebaseGameObjectInput(location.toLatLng(), this, view.getImageResolver())
        } else {
            gameObjectInput?.queryCenter = location.toLatLng()
        }
    }

    override fun gameObjectArrived(key: String, gameObject: IGameObject) {
        keyToGameObject[key] = gameObject
        view.addGameObject(gameObject)
    }

    override fun gameObjectMoved(key: String, newLocation: LatLng) {
        keyToGameObject[key]?.location = newLocation
    }

    override fun gameObjectDeleted(key: String) {
        val gameObject = keyToGameObject[key] ?: return
        keyToGameObject -= key
        view.removeGameObject(gameObject)
    }
}
