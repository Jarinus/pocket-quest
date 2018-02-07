package nl.pocketquest.pocketquest.utils

import kotlin.math.pow

fun Long.withSuffix() = when {
    this < 1000 -> "$this"
    else -> {
        val exp = (Math.log(this.toDouble()) / Math.log(1000.0)).toInt()
        val remainder = this / 1000.0.pow(exp.toDouble())
        val formatter = if (remainder > 100) "%.0f%c" else "%.1f%c"
        formatter.format(remainder,"kMGTPE"[exp - 1])
    }
}
