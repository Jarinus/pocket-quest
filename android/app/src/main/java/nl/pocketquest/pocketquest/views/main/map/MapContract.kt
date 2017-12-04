package nl.pocketquest.pocketquest.views.main.map

import android.graphics.Bitmap
import android.location.Location
import nl.pocketquest.pocketquest.game.IGameObject
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView

class MapContract {
    interface MapView : BaseView {
        /**
         * Focuses the Center of the map to the specified location
         *
         * @param location The location to center the map on
         */
        fun focusMapCenterOn(location: Location)

        /**
         * Adds a GameObject to the view. The view is responsible for drawing this GameObject.
         * It also needs to observe the gameObject and respond appropriately to changes
         *
         * @param gameObject The game object to display
         */
        fun addGameObject(gameObject: IGameObject)

        /**
         * Removes a GameObject from the view. After calling this operation the gameObject must
         * not be drawn anymore
         *
         * @param gameObject The game object to remove
         */
        fun removeGameObject(gameObject: IGameObject)

        fun displayNotification(text: String)

        fun setRightCornerImage(bitmap: Bitmap)

        fun setRightCornerImageVisibility(visible: Boolean)

        fun getImageResolver(): ImageResolver
    }

    abstract class MapPresenter(mapView: MapView) : BasePresenter<MapView>(mapView) {
        abstract fun onGameObjectClicked(gameObject: IGameObject): Boolean
        abstract fun onMapReady()
        abstract fun onLocationChanged(location: Location)
    }
}
