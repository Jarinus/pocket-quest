package nl.pocketquest.pocketquest.views.map

import android.location.Location
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.ClickableGameObject
import nl.pocketquest.pocketquest.game.GameObject
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
class MapPresenter(mapView: MapContract.MapView) : MapContract.MapPresenter(mapView) {

    private var cachedLocation: Location? = null
    private var ready = false
    private var player: GameObject? = null

    private fun createPlayerMarker(): GameObject {
        val frames = SpriteSheetCreator(view.decodeResource(R.drawable.santasprite), 4 xy 4)
                .frames
                .map { it.padded(1.1 xy 1.7) }

        val player = GameObject(0 latLong 0, frames.first()).also {
            GameObjectAnimator(it, frames, ANIMATION_DURATION).start()
        }
        return player
    }

    override fun onGameObjectClicked(gameObject: GameObject) {
        view.displayToast("GameObject was clicked $gameObject")
        (gameObject as ClickableGameObject)?.clicked()
    }

    override fun onMapReady() {
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
    }
}
