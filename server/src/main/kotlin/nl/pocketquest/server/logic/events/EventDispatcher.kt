package nl.pocketquest.server.logic.events

/**
 * Responsible for the executing of events and managing the eventpool
 */
interface EventDispatcher {

    /**
     * Set up the event dispatcher to dispatch events of the eventpool
     */
    fun register(eventPool: MutableEventPool)

    fun register(eventHandler: EventHandler<*, *>)

    fun eventArrived(event: Event<*, *>)
}