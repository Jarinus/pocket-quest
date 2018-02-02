package nl.pocketquest.pocketquest.utils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewManager
import android.widget.ImageView
import org.jetbrains.anko.custom.ankoView

class SquareImageView(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context,attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val minSize  = if (widthSize <= heightSize) widthMeasureSpec else heightMeasureSpec
        // If one of the measures is match_parent, use that one to determine the size.
        // If not, use the default implementation of onMeasure.

        when {
            widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY -> setMeasuredDimension(widthSize, widthSize)
            heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY -> setMeasuredDimension(heightSize, heightSize)
            else -> setMeasuredDimension(minSize, minSize)
        }
    }
}

inline fun ViewManager.squaredImageView(theme : Int = 0, init: SquareImageView.()->Unit)
        = ankoView({SquareImageView(it)}, theme = theme, init = init)
