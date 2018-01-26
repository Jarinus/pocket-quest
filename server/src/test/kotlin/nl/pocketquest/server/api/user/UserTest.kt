package nl.pocketquest.server.api.user

import com.github.salomonbrys.kodein.Kodein
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.testhelpers.MockDataSource
import org.junit.Test

import org.junit.Assert.*

class UserTest {

    @Test
    fun testSetStatus() {
        val source = MockDataSource(Status.IDLE.externalName)
        val kodein = Kodein {}
        val user = User(source, Inventory(listOf("test", "user", "inventory"), kodein))
        runBlocking {
            assertTrue(user.setStatus(Status.GATHERING))
            assertEquals(Status.GATHERING.externalName, source.content)
            assertFalse(user.setStatus(Status.GATHERING))
            assertEquals(Status.GATHERING.externalName, source.content)
            assertTrue(user.setStatus(Status.IDLE))
            assertEquals(Status.IDLE.externalName, source.content)
        }
    }
}
