package nl.pocketquest.pocketquest

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.sprites.GameObjectAnimator
import org.jetbrains.anko.AnkoLogger

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), AnkoLogger{
    private fun GameObject.animate(frames: Sequence<Bitmap>, duration: Int)
            = GameObjectAnimator(this@BaseActivity, this, frames, duration).apply { start() }

    private fun GameObject.load
}