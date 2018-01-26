package nl.pocketquest.server.testhelpers

import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventDispatcher
import nl.pocketquest.server.logic.events.EventHandler
import nl.pocketquest.server.logic.events.MutableEventPool

class NoOpDispatcher : EventDispatcher {
    override fun register(eventPool: MutableEventPool) = Unit

    override fun register(eventHandler: EventHandler<*, *>) = Unit

    override fun eventArrived(event: Event<*, *>) = Unit
}