package nl.pocketquest.server.utils

import com.google.firebase.database.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.suspendCoroutine

fun <T> loopAsynchronous(
        context: CoroutineContext,
        sleepDuration: Number = 0,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
) = async(context, start) {
    while (isActive) {
        block()
        delay(sleepDuration.toLong())
    }
}

class WrappedContinuation<in T>(private val c: Continuation<T>) : Continuation<T> {
    private var isResolved = false
    override val context: CoroutineContext
        get() = c.context

    override fun resume(value: T) {
        if (!isResolved) {
            isResolved = true
            c.resume(value)
        }
    }

    override fun resumeWithException(exception: Throwable) {
        if (!isResolved) {
            isResolved = true
            c.resumeWithException(exception)
        }
    }
}

inline suspend fun <T> suspendCoroutineW(crossinline block: (WrappedContinuation<T>) -> Unit): T =
        suspendCoroutine { c ->
            val wd = WrappedContinuation(c)
            block(wd)
        }

data class TransactionResult<T>(
        val value: T?,
        val abort: Boolean
) {
    companion object {
        fun <T> abort() = TransactionResult<T>(null, true)
        fun <T> success(value: T?) = TransactionResult<T>(value, false)
    }
}

suspend inline fun <reified T> DatabaseReference.readAsync(): T = readFromDatabaseAsync(this)
suspend inline fun <reified T> DatabaseReference.writeAsync(t: T) = writeToDatabaseAsync(this, t)
suspend inline fun <reified T> DatabaseReference.transaction(crossinline transformer: (T?) -> TransactionResult<T>) = doTransaction(this, transformer)
suspend inline fun <reified T> DatabaseReference.transactionNotNull(crossinline transformer: (T) -> TransactionResult<T>) = doTransaction<T>(this) {
    it?.let(transformer) ?: TransactionResult.success(it)
}

suspend fun DatabaseReference.incrementByOrCreate(increment: Long, initialValue: Long): Boolean = doTransaction<Long>(this) {
    TransactionResult.success(when (it) {
        null -> initialValue
        else -> it + increment
    })
}

suspend fun DatabaseReference.incrementBy(increment: Long, min: Long, max: Long): Boolean = transactionNotNull<Long> {
    val range = LongRange(min, max)
    val newValue = it + increment
    if (range.contains(newValue)) {
        TransactionResult.success(newValue)
    } else {
        TransactionResult.abort()
    }
}

suspend fun DatabaseReference.remove(): Boolean = suspendCoroutineW { d ->
    this.removeValue { error, ref -> d.resume(error == null) }
}

suspend inline fun <reified T> doTransaction(dbref: DatabaseReference, crossinline transformer: (T?) -> TransactionResult<T>): Boolean = suspendCoroutineW { d ->

    dbref.runTransaction(object : Transaction.Handler {
        override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
            d.resume(committed)
        }

        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val currentValue = currentData.getValue(T::class.java)
            val result = transformer(currentValue)
            return when (result.abort) {
                true -> Transaction.abort()
                false -> {
                    currentData.value = result.value
                    Transaction.success(currentData)
                }
            }
        }
    })

}

suspend inline fun <reified T> readFromDatabaseAsync(dbref: DatabaseReference): T = suspendCoroutineW { d ->
    dbref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(e: DatabaseError?) {
            d.resumeWithException(e?.toException() ?: Exception("cancelled"))
        }

        override fun onDataChange(snapshot: DataSnapshot) = try {
            val ti: GenericTypeIndicator<T> = object : GenericTypeIndicator<T>() {}
            val data: T? = snapshot.getValue(ti)
            if (data != null) {
                d.resume(data)
            } else {
                val errmsg =
                        if (snapshot.value == null)
                            "data missing"
                        else
                            "invalid read data format"
                d.resumeWithException(Exception(errmsg))
            }
        } catch (e: Exception) {
            d.resumeWithException(e)
        }
    })
}

suspend fun writeToDatabaseAsync(dbref: DatabaseReference, data: Any?): String = suspendCoroutineW { d ->
    dbref.setValue(data, { databaseError, databaseReference ->
        if (databaseError != null) {
            d.resumeWithException(databaseError.toException())
        } else {
            d.resume(databaseReference.key) //return the key that was written
        }
    })
}

suspend fun writeMultiToDatabaseAsync(dbref: DatabaseReference, data: Map<String, Any?>): String = suspendCoroutineW { d ->
    dbref.updateChildren(data, { databaseError, databaseReference ->
        if (databaseError != null) {
            d.resumeWithException(databaseError.toException())
        } else {
            d.resume(databaseReference.key ?: "")
        }
    })
}
