package nl.pocketquest.pocketquest.views.main.map

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.AnimatedGameObject
import nl.pocketquest.pocketquest.game.Clickable
import nl.pocketquest.pocketquest.game.FirebaseGameObjectInput
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.construction.GameObjectAcceptor
import nl.pocketquest.pocketquest.sprites.GameObjectAnimator
import nl.pocketquest.pocketquest.sprites.SpriteSheetCreator
import nl.pocketquest.pocketquest.utils.latLong
import nl.pocketquest.pocketquest.utils.toGoogleLatLng
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

        val animator = GameObjectAnimator(frames, ANIMATION_DURATION)
        val player = AnimatedGameObject(0 latLong 0, frames.first(), animator)
        animator.start()
        return player
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
        if (ready) return
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
        player?.location = location.toGoogleLatLng()
        view.focusMapCenterOn(location)
        createOrUpdateGeoQuery(location)
    }

    private fun createOrUpdateGeoQuery(location: Location) {
        if (gameObjectInput == null) {
            gameObjectInput = FirebaseGameObjectInput(location.toGoogleLatLng(), this, view.getImageResolver())
        } else {
            gameObjectInput?.queryCenter = location.toGoogleLatLng()
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
