package nl.pocketquest.server.logic.events

interface EventHandler<Type, Data> {

    val typeClass: Class<Type>
    val dataClass: Class<Data>

    fun isRelevant(type: Type): Boolean

    suspend fun handle(event: Event<Type, Data>): Unit

}