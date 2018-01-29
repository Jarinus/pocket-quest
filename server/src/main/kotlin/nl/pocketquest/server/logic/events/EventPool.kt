package nl.pocketquest.server.logic.events

interface EventPool {

    fun submit(event: Event<*, *>)

    /**
     * Must return true if the event has been submitted and has not yet been executed
     */
    fun contains(event: Event<*, *>): Boolean
}

interface MutableEventPool : EventPool {

    fun remove(event: Event<*, *>): Boolean

    fun close()
}
