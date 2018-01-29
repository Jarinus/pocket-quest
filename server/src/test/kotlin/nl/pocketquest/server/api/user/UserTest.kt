package nl.pocketquest.server.api.user

import com.github.salomonbrys.kodein.Kodein
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.testhelpers.MockDataSource
import org.junit.Test

import org.junit.Assert.*

class UserTest {

    @Test
    fun shouldSetStatus() {
        val source = MockDataSource(Status.IDLE.identifier)
        val kodein = Kodein {}
        val user = User(source, Inventory(listOf("test", "user", "inventory"), kodein))
        runBlocking {
            assertTrue(user.setStatus(Status.GATHERING))
            assertEquals(Status.GATHERING.identifier, source.content)
            assertFalse(user.setStatus(Status.GATHERING))
            assertEquals(Status.GATHERING.identifier, source.content)
            assertTrue(user.setStatus(Status.IDLE))
            assertEquals(Status.IDLE.identifier, source.content)
        }
    }
}
