package nl.pocketquest.server.api.user

enum class Status(
        val externalName: String,
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
                .find { it.externalName == name }
    }
}