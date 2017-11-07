package nl.pocketquest.pocketquest.views.map

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.ClickableGameObject
import nl.pocketquest.pocketquest.game.FirebaseGameObjectInput
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.construction.GameObjectAcceptor
import nl.pocketquest.pocketquest.game.entities.Entities
import nl.pocketquest.pocketquest.sprites.GameObjectAnimator
import nl.pocketquest.pocketquest.sprites.SpriteSheetCreator
import nl.pocketquest.pocketquest.sprites.padded
import nl.pocketquest.pocketquest.utils.latLong
import nl.pocketquest.pocketquest.utils.toLatLng
import nl.pocketquest.pocketquest.utils.xy

const val ANIMATION_DURATION = 42

/**
 * Created by Laurens on 6-11-2017.
 */
class MapPresenter(mapView: MapContract.MapView) : MapContract.MapPresenter(mapView), GameObjectAcceptor {

    private var cachedLocation: Location? = null
    private var ready = false
    private var player: IGameObject? = null
    private val keyToGameObject = mutableMapOf<String, IGameObject>()
    private var gameObjectInput: FirebaseGameObjectInput? = null

    private fun createPlayerMarker(): IGameObject {
        val frames = SpriteSheetCreator(view.decodeResource(R.drawable.santasprite), 4 xy 4)
                .frames
                .map { it.padded(1.1 xy 1.7) }

        val player = GameObject(0 latLong 0, frames.first()).also {
            GameObjectAnimator(it, frames, ANIMATION_DURATION).start()
        }
        return player
    }

    override fun onGameObjectClicked(gameObject: IGameObject) {
        view.displayToast("GameObject was clicked $gameObject")
        (gameObject as? ClickableGameObject)?.clicked()
    }

    override fun onMapReady() {
        Entities.items
        ready = true
        cachedLocation?.also { setNewLocation(it) }
        player = createPlayerMarker()
        player?.also { view.addGameObject(it) }
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
