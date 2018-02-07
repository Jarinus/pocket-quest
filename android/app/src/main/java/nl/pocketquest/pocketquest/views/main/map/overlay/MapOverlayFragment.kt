package nl.pocketquest.pocketquest.views.main.map.overlay

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_map_overlay.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.entities.FirebaseImageResolver
import nl.pocketquest.pocketquest.game.entities.ImageResolver
import nl.pocketquest.pocketquest.views.BaseFragment
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.ctx

class MapOverlayFragment : BaseFragment(), MapOverlayContract.MapOverlayView {

    private val presenter: MapOverlayContract.MapOverlayPresenter = MapOverlayPresenter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        presenter.onAttach()
        return inflater.inflate(R.layout.fragment_map_overlay, container, false)
    }

    override fun displayNotification(text: String) = ctx.runOnUiThread {
        snackbar(view!!, text)
    }

    override fun setRightCornerImage(bitmap: Bitmap) {
        async(UI) {
            imageIcon.setImageBitmap(bitmap)
            imageIcon.invalidate()
        }
    }

    override fun setRightCornerImageVisibility(visible: Boolean) {
        async(UI) {
            imageIcon.visibility = when (visible) {
                true -> View.VISIBLE
                false -> View.INVISIBLE
            }
        }
    }

    override fun getImageResolver() = object : ImageResolver {
        suspend override fun resolveImage(imageID: String) =
                FirebaseImageResolver.resolveImage(ctx, imageID)
    }
}
