package nl.pocketquest.pocketquest.mvp

import org.jetbrains.anko.AnkoLogger

open class BasePresenter<out View : BaseView>(protected val view: View) : AnkoLogger
