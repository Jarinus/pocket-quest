package nl.pocketquest.pocketquest.utils

/**
 * Created by thijs on 24-10-2017.
 */
operator fun Number.times(other: Number) : Number = toDouble() * other.toDouble()
operator fun Number.div(other: Number) : Number = toDouble() / other.toDouble()