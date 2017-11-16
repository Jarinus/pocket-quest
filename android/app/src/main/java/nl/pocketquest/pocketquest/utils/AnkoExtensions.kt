package nl.pocketquest.pocketquest.utils

import android.view.ViewManager
import nl.pocketquest.pocketquest.views.main.inventory.SquaredRelativeLayout
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.squaredRelativeLayout(theme: Int = 0) = squaredRelativeLayout(theme){}
inline fun ViewManager.squaredRelativeLayout(theme: Int = 0, init: SquaredRelativeLayout.()->Unit)
        = ankoView( {SquaredRelativeLayout(it)} , theme, init)
