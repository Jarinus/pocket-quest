package nl.pocketquest.server.logic.events.impl

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventDispatcher
import nl.pocketquest.server.logic.events.EventHandler
import nl.pocketquest.server.logic.events.MutableEventPool
import java.util.*
import kotlin.concurrent.timerTask

class DefaultEventDispatcher : EventDispatcher {

    private val handlers = mutableSetOf<EventHandler<*, *>>()
    private lateinit var eventPool: MutableEventPool
    private val timer = Timer()

    override fun register(eventPool: MutableEventPool) {
        this.eventPool = eventPool
    }

    override fun register(eventHandler: EventHandler<*, *>) {
        handlers.add(eventHandler)
    }

    override fun eventArrived(event: Event<*, *>) {
        timer.schedule(timerTask { dispatch(event) }, Date(event.scheduledFor))
    }

    private fun dispatch(event: Event<*, *>) {
        eventPool.remove(event)
        consumeByHandlers(event)
    }

    private fun <T, D> consumeByHandlers(event: Event<T, D>) {
        val capableHandlers: List<EventHandler<T, D>> = handlers.filter {
            it.dataClass == event.dataType &&
                    it.typeClass == event.eventType
        }.map {
            it as EventHandler<T, D>
        }
        async(CommonPool) {
            capableHandlers.forEach {
                if (it.isRelevant(event.type)) {
                    it.handle(event)
                }
            }
        }
    }
}