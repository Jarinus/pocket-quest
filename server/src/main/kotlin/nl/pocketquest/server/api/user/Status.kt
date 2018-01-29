package nl.pocketquest.server.api.user

enum class Status(
        val identifier: String,
        val statusChangeValidator: (Status) -> Boolean
) {

    GATHERING("gathering", {
        it == IDLE
    }),
    IDLE("idle", {
        true
    });

    companion object {
        fun fromExternalName(name: String) = Status.values()
                .find { it.identifier == name }
    }
}