package nl.pocketquest.server.utils

import com.google.firebase.database.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.pocketquest.server.dataaccesslayer.TransactionResult
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


suspend inline fun <reified T> DatabaseReference.readAsync(): T? = readFromDatabaseAsync(this)
suspend fun <T> DatabaseReference.readAsync(expectedType: Class<T>): T? = readFromDatabaseAsync(this, expectedType)
suspend fun <T> DatabaseReference.writeAsync(t: T) = writeToDatabaseAsync(this, t)
suspend fun <T> DatabaseReference.transaction(expectedType: Class<T>, transformer: (T?) -> TransactionResult<T>) = doTransaction(this, expectedType, transformer)
suspend inline fun <reified T> DatabaseReference.transactionNotNull(crossinline transformer: (T) -> TransactionResult<T>) = doTransaction<T>(this, T::class.java) {
    it?.let(transformer) ?: TransactionResult.success(it)
}

suspend fun DatabaseReference.incrementByOrCreate(increment: Long, initialValue: Long): Boolean = doTransaction<Long>(this, Long::class.java) {
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

suspend fun <T> doTransaction(dbref: DatabaseReference, expectedType: Class<T>, transformer: (T?) -> TransactionResult<T>): Boolean = suspendCoroutineW { d ->

    dbref.runTransaction(object : Transaction.Handler {
        override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
            d.resume(committed)
        }

        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val currentValue = currentData.getValue(expectedType)
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

suspend inline fun <reified T> readFromDatabaseAsync(dbref: DatabaseReference): T? = readFromDatabaseAsync(dbref, T::class.java)

suspend fun <T> readFromDatabaseAsync(dbref: DatabaseReference, expectedType: Class<T>): T? = suspendCoroutineW { d ->
    dbref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(e: DatabaseError?) {
            getLogger().error("Database exception")
            d.resumeWithException(e?.toException() ?: Exception("cancelled"))
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            val data: T?
            try {
                data = snapshot.getValue(expectedType)
                d.resume(data)
            } catch (e: Exception) {
                d.resumeWithException(e)
            }
        }
    })
}

suspend fun writeToDatabaseAsync(dbref: DatabaseReference, data: Any?): Boolean = suspendCoroutineW { d ->
    dbref.setValue(data, { databaseError, databaseReference ->
        if (databaseError != null) {
            d.resumeWithException(databaseError.toException())
            d.resume(false)
        } else {
            d.resume(true)
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
