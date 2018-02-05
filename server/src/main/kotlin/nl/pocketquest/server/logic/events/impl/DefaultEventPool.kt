package nl.pocketquest.server.logic.events.impl

import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventDispatcher
import nl.pocketquest.server.logic.events.EventPool
import nl.pocketquest.server.logic.events.MutableEventPool

class DefaultEventPool(private val eventDispatcher: EventDispatcher) : MutableEventPool {

    init {
        eventDispatcher.register(this)
    }

    private val events = mutableSetOf<Event<*, *>>()

    override fun submit(event: Event<*, *>) {
        events.add(event)
        eventDispatcher.eventArrived(event)
    }

    override fun contains(event: Event<*, *>) = events.contains(event)

    override fun remove(event: Event<*, *>) = events.remove(event)

    override fun close() = Unit

    fun clear() = events.clear()

    fun events(): Set<Event<*, *>> = events

    override fun empty() = events.isEmpty()
}