package nl.pocketquest.server.testhelpers

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventDispatcher
import nl.pocketquest.server.logic.events.EventHandler
import nl.pocketquest.server.logic.events.MutableEventPool
import nl.pocketquest.server.utils.getLogger
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiPredicate

class TestEventDispatcher : EventDispatcher {
    private lateinit var eventPool: MutableEventPool
    private val handlers = mutableSetOf<EventHandler<*, *>>()
    private val index = AtomicInteger(0)
    private val events = mutableMapOf<Pair<Long, Int>, Event<*, *>>()


    override fun register(eventPool: MutableEventPool) {
        this.eventPool = eventPool
    }

    override fun register(eventHandler: EventHandler<*, *>) {
        handlers.add(eventHandler)
    }

    override fun eventArrived(event: Event<*, *>) {
        events[event.scheduledFor to index.getAndIncrement()] = event
    }

    private fun firstInQue(): Pair<Pair<Long, Int>, Event<*, *>>? {
        if (events.isEmpty()) {
            return null
        }
        val firstKey = events.toSortedMap(compareBy({ it.first }, { it.second })).firstKey()
                ?: return null
        return firstKey to events[firstKey]!!
    }

    suspend fun executeNext(): Boolean {
        val event = firstInQue() ?: return false
        fireEvent(event)
        return true
    }

    private suspend fun fireEvent(event: Pair<Pair<Long, Int>, Event<*, *>>) {
        getLogger().debug("Firing event of type {}", event.second.type)
        events -= event.first
        dispatch(event.second)
    }

    suspend fun runFully() = runUntil { false }

    suspend fun runUntil(predicate: (Pair<Pair<Long, Int>, Event<*, *>>) -> Boolean) {
        generateSequence { firstInQue() }
                .takeWhile { !predicate(it) }
                .forEach { fireEvent(it) }
    }

    suspend fun runUntilEventOfType(type: Any) =
            runUntil {
                it.second.type == type
            }

    suspend fun runUntilEvent(event: Event<*, *>) = runUntil {
        it.second == event
    }

    private suspend fun dispatch(event: Event<*, *>) {
        eventPool.remove(event)
        consumeByHandlers(event)
    }

    private suspend fun <T, D> consumeByHandlers(event: Event<T, D>) {
        val capableHandlers: List<EventHandler<T, D>> = handlers.filter {
            it.dataClass == event.dataType &&
                    it.typeClass == event.eventType
        }.map {
            it as EventHandler<T, D>
        }
        capableHandlers.forEach {
            if (it.isRelevant(event.type)) {
                it.handle(event)
            }
        }
    }
}