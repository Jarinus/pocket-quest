package nl.pocketquest.pocketquest.utils

import android.widget.SeekBar

fun SeekBar.onProgressChange(handler: (Int) -> Unit) {
    this.setOnSeekBarChangeListener(object: SeekBarChangeListenerWithDefaults {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            handler(progress)
        }
    })
}

private interface SeekBarChangeListenerWithDefaults : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
