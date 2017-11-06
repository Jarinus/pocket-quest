package nl.pocketquest.pocketquest.views.map

import android.location.Location
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView

class MapContract {

    interface MapView : BaseView {
        /**
         * Focuses the Center of the map to the specified location
         */
        fun focusMapCenterOn(location: Location)

        /**
         * Adds a GameObject to the view. The view is responsible for drawing this GameObject.
         * It also needs to observe the gameObject and respond appropriately to changes
         */
        fun addGameObject(gameObject: GameObject)

        /**
         * Removes a GameObject from the view. After calling this operation the gameObject must
         * not be drawn anymore
         */
        fun removeGameObject(gameObject: GameObject)
    }

    abstract class MapPresenter(mapView: MapView) : BasePresenter<MapView>(mapView) {
        abstract fun onGameObjectClicked(gameObject: GameObject)
        abstract fun onMapReady()
        abstract fun onLocationChanged(location: Location)
    }
}
