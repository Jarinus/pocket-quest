package nl.pocketquest.pocketquest.mvp

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import nl.pocketquest.pocketquest.utils.decodeResource
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), AnkoLogger, BaseView {

    override fun displayToast(message: String) = toast(message)

    override fun decodeResource(resourceID: Int) = applicationContext.decodeResource(resourceID)
}
