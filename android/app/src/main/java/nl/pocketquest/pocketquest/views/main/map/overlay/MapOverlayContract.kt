package nl.pocketquest.pocketquest.views.main.map.overlay

import android.graphics.Bitmap
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.mvp.BasePresenter
import nl.pocketquest.pocketquest.mvp.BaseView

class MapOverlayContract {
    interface MapOverlayView : BaseView {
        fun displayNotification(text: String)

        fun setRightCornerImage(bitmap: Bitmap)

        fun setRightCornerImageVisibility(visible: Boolean)

        fun getImageResolver(): ImageResolver
    }

    abstract class MapOverlayPresenter(mapOverlayView: MapOverlayView) : BasePresenter<MapOverlayView>(mapOverlayView)
}
