package nl.pocketquest.pocketquest.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

fun whenLoggedIn(toExecute: (FirebaseUser) -> Unit) {
    FirebaseAuth.getInstance().currentUser?.also(toExecute)
}

fun whenNotLoggedIn(toExecute: () -> Unit) {
    FirebaseAuth.getInstance().currentUser ?: toExecute()
}
