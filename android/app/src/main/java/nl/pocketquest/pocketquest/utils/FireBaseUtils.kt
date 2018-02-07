package nl.pocketquest.pocketquest.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

val AUTH get() = FirebaseAuth.getInstance()
val STORAGE get() = FirebaseStorage.getInstance()
val DATABASE get() = FirebaseDatabase.getInstance()

inline fun <reified T> DataSnapshot.getValue() = getValue(T::class.java)

fun whenLoggedIn(toExecute: (FirebaseUser) -> Unit) {
    AUTH.currentUser?.also(toExecute)
}

suspend fun getTimeOfset() = DATABASE.getReference(".info/serverTimeOffset").readAsync<Double>()

fun whenNotLoggedIn(toExecute: () -> Unit) {
    AUTH.currentUser ?: toExecute()
}

inline fun <reified T> DatabaseReference.listen(crossinline consumer: (T) -> Unit) {
    this.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError?) = Unit

        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(T::class.java) ?: return
            consumer(value)
        }
    })
}
