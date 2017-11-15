package nl.pocketquest.pocketquest.mvp

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import nl.pocketquest.pocketquest.utils.decodeResource
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), AnkoLogger, BaseView {
    override fun displayToast(message: String) = toast(message)

    override fun decodeResource(resourceID: Int) = applicationContext.decodeResource(resourceID)
}

open class BaseFragment : Fragment(), AnkoLogger, BaseView {
    override fun displayToast(message: String) = toast(message)

    override fun decodeResource(resourceID: Int) = ctx.decodeResource(resourceID)
}
