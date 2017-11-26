package nl.pocketquest.server.user

enum class Status(
        val firebaseName: String,
        val statusChangeValidator: (Status) -> Boolean
) {

    GATHERING("gathering", {
        it == IDLE
    }),
    IDLE("idle", {
        true
    });

    companion object {
        fun fromFirebaseName(name: String) = Status.values()
                .find { name == it.firebaseName }
    }
}