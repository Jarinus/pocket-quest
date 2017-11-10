package nl.pocketquest.pocketquest.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

val AUTH get() = FirebaseAuth.getInstance()
val STORAGE get() = FirebaseStorage.getInstance()
val DATABASE get() = FirebaseDatabase.getInstance()

inline fun <reified T> DataSnapshot.getValue() = getValue(T::class.java)

fun whenLoggedIn(toExecute: (FirebaseUser) -> Unit) {
    AUTH.currentUser?.also(toExecute)
}

fun whenNotLoggedIn(toExecute: () -> Unit) {
    AUTH.currentUser ?: toExecute()
}
