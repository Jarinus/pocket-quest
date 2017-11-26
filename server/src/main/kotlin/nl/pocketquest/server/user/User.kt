package nl.pocketquest.server.user

import com.google.firebase.database.Transaction
import nl.pocketquest.server.utils.DATABASE
import nl.pocketquest.server.utils.TransactionResult
import nl.pocketquest.server.utils.transaction

class User(private val id: String) {

    private val statusRef = DATABASE.getReference("users/$id/status")

    suspend fun setStatus(newStatus: Status) = statusRef.transaction<String> { currentValue ->
        if (currentValue == null) {
            return@transaction TransactionResult.success(newStatus.firebaseName)
        } else {
            val currentStatus = Status.fromFirebaseName(currentValue)
            if (currentStatus != null && currentStatus.statusChangeValidator(newStatus)) {
                return@transaction TransactionResult.success(newStatus.firebaseName)
            }
            return@transaction TransactionResult.abort()
        }
    }

}