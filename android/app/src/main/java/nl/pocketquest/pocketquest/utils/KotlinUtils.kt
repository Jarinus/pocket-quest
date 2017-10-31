package nl.pocketquest.pocketquest.utils

operator fun Number.times(other: Number): Number = toDouble() * other.toDouble()
operator fun Number.div(other: Number): Number = toDouble() / other.toDouble()
