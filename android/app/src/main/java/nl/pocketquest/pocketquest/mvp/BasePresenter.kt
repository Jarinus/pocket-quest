package nl.pocketquest.pocketquest.mvp

open class BasePresenter<out View : BaseView>(protected val view: View)
