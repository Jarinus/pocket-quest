package nl.pocketquest.pocketquest.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage

val AUTH get() = FirebaseAuth.getInstance()
val STORAGE get() = FirebaseStorage.getInstance()

fun whenLoggedIn(toExecute: (FirebaseUser) -> Unit) {
    AUTH.currentUser?.also(toExecute)
}

fun whenNotLoggedIn(toExecute: () -> Unit) {
    AUTH.currentUser ?: toExecute()
}
