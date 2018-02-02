package nl.pocketquest.pocketquest.mvp

import org.jetbrains.anko.AnkoLogger

abstract class BasePresenter<out View : BaseView>(protected val view: View) : AnkoLogger{
    abstract fun onAttach()
    abstract fun onDetach()
}
