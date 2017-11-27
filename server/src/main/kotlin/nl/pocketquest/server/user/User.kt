package nl.pocketquest.server.user

import com.google.firebase.database.Transaction
import nl.pocketquest.server.utils.DATABASE
import nl.pocketquest.server.utils.TransactionResult
import nl.pocketquest.server.utils.transaction

suspend fun updateUser(id: String, update: suspend User.() -> Unit) {
    User(id).update()
}

class User(private val id: String) {

    private val statusRef = DATABASE.getReference("users/$id/status")

    suspend fun setStatus(newStatus: Status) = statusRef.transaction<String> { currentValue ->
        return@transaction if (currentValue == null) {
            TransactionResult.success(newStatus.firebaseName)
        } else {
            Status.fromFirebaseName(currentValue)
                    ?.takeIf { it.statusChangeValidator(newStatus) }
                    ?.let { TransactionResult.success(it.firebaseName) }
                    ?: TransactionResult.abort()
        }
    }
}