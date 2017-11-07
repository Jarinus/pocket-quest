package nl.pocketquest.pocketquest.utils

import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
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

class WrappedContinuation<T>(val c: Continuation<T>) : Continuation<T> {
    var isResolved = false
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

suspend inline fun <reified T> DatabaseReference.readAsync(): T = readFromDatabaseAsync(this)

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
    dbref.setValue(data, DatabaseReference.CompletionListener { databaseError, databaseReference ->
        if (databaseError != null) {
            d.resumeWithException(databaseError.toException())
        } else {
            d.resume(databaseReference.key) //return the key that was written
        }
    })
}

suspend fun writeMultiToDatabaseAsync(dbref: DatabaseReference, data: Map<String, Any?>): String = suspendCoroutineW { d ->
    dbref.updateChildren(data, DatabaseReference.CompletionListener { databaseError, databaseReference ->
        if (databaseError != null) {
            d.resumeWithException(databaseError.toException())
        } else {
            d.resume(databaseReference.key ?: "")
        }
    })
}

suspend fun uploadFileToStorageAsync(dest_sref: StorageReference,
                                     srcFileUri: Uri,
                                     metadata: StorageMetadata?,
                                     progressListener: OnProgressListener<in UploadTask.TaskSnapshot>? = null): String = suspendCoroutineW { d ->
    val uploadTask = dest_sref.putFile(srcFileUri, metadata ?: StorageMetadata())
    uploadTask.addOnFailureListener { e ->
        d.resumeWithException(e)
    }
    uploadTask.addOnSuccessListener { snapshot ->
        d.resume(snapshot.storage.toString()) //return the download path
    }
    (progressListener)?.let { uploadTask.addOnProgressListener(progressListener) }
}

suspend fun deleteFileFromStorageAsync(dest_sref: StorageReference): String = suspendCoroutineW { d ->
    //for documentation see https://firebase.google.com/docs/storage/android/delete-files
    val deleteTask = dest_sref.delete()
    deleteTask.addOnFailureListener { e ->
        d.resumeWithException(e)
    }
    deleteTask.addOnSuccessListener { snapshot ->
        d.resume(dest_sref.toString())
    }
}
