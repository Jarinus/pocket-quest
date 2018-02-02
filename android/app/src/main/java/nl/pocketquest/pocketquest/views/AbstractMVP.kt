package nl.pocketquest.pocketquest.views

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger

/*
 * The [BaseView] should be implemented by all the views.
 * This can be done by either extending , through the usage of either [BaseActivity] or [BaseFragment]
 */
interface BaseView : AnkoLogger
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), AnkoLogger, BaseView
open class BaseFragment : Fragment(), AnkoLogger, BaseView

/*
 * The [BasePresenter] should be implemented by all the presenters.
 */
abstract class BasePresenter<out View : BaseView>(protected val view: View) : AnkoLogger{
    abstract fun onAttach()
    abstract fun onDetach()
}

