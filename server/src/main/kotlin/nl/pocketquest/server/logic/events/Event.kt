package nl.pocketquest.server.logic.events

data class Event<Type, Data>(
        val type: Type,
        val data: Data,
        val eventType: Class<Type>,
        val dataType: Class<Data>,
        val scheduledFor: Long
) {

    companion object {
        inline fun <reified Type, reified Data> of(type: Type, data: Data, scheduledFor: Long) =
                Event(type, data, Type::class.java, Data::class.java, scheduledFor)
    }
}