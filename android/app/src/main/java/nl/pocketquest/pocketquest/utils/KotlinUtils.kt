package nl.pocketquest.pocketquest.utils

operator fun Number.times(other: Number): Number = toDouble() * other.toDouble()
operator fun Number.div(other: Number): Number = toDouble() / other.toDouble()

class RepeatingSequence<T> constructor(val sequence : Sequence<T>) : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        var iterator = sequence.iterator()
        override fun next(): T {
            val result = iterator.next()
            if (!iterator.hasNext()) {
                iterator = sequence.iterator()
            }
            return result
        }

        override fun hasNext() = iterator.hasNext()
    }
}

fun <T> Sequence<T>.repeat() : Sequence<T> = RepeatingSequence(this)
