package nl.pocketquest.pocketquest.utils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewManager
import android.widget.GridView
import org.jetbrains.anko.custom.ankoView

class FullHeightGridView : GridView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2,
                MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}

inline fun ViewManager.fullHeightGridView(init: FullHeightGridView.() -> Unit): FullHeightGridView {
    return ankoView({FullHeightGridView(it)}, theme = 0, init = init)
}
