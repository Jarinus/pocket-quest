package nl.pocketquest.pocketquest.views.main.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import nl.pocketquest.pocketquest.mvp.BaseFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

/**
 * Created by Laurens on 13-11-2017.
 */
class InventoryFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            verticalLayout {
                textView("Hello from inventory")
            }
        }.view
    }
}

class MyActivityUI : AnkoComponent<InventoryFragment> {
    override fun createView(ui: AnkoContext<InventoryFragment>) = with(ui) {
        verticalLayout {
            val name = editText()
            button("Say Hello") {
                onClick { ctx.toast("Hello, ${name.text}!") }
            }
        }
    }
}
