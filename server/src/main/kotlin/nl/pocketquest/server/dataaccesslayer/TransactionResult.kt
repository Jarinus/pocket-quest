package nl.pocketquest.server.dataaccesslayer

data class TransactionResult<T>(
        val value: T?,
        val abort: Boolean
) {
    companion object {
        fun <T> abort() = TransactionResult<T>(null, true)
        fun <T> success(value: T?) = TransactionResult<T>(value, false)
    }
}