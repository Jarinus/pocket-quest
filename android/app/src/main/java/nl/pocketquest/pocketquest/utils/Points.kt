package nl.pocketquest.pocketquest.utils

infix fun Number.xy(y: Number) = Point(this, y)
infix fun Double.xy(y: Double) = DoublePoint(this, y)
infix fun Int.xy(y: Int) = IntPoint(this, y)

open class Point(open val x: Number, open val y: Number) {
    operator fun div(other: Point): Point = (x / other.x) xy (y / other.y)
    operator fun times(other: Point): Point = (x * other.x) xy (y * other.y)
    open operator fun Point.component1() = x
    open operator fun Point.component2() = y
}

fun Point.toNumberPoint() = this as? Point ?: Point(x, y)
fun Point.toIntPoint() = this as? IntPoint ?: IntPoint(x.toInt(), y.toInt())
fun Point.toDoublePoint() = this as? DoublePoint ?: Point(x.toDouble(), y.toDouble())

data class DoublePoint(override val x: Double, override val y: Double) : Point(x, y)
data class IntPoint(override val x: Int, override val y: Int) : Point(x, y) {
    operator fun div(other: IntPoint) = (x / other.x) xy (y / other.y)
    operator fun times(other: IntPoint) = (x * other.x) xy (y * other.y)
    fun allSmallerPoints() = (0 until y).asSequence().flatMap { y ->
        (0 until x).asSequence().map { x -> x xy y }
    }
}
