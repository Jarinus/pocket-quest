package nl.pocketquest.server.logic.request.handler.impl

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.TestDB
import nl.pocketquest.server.api.TestableTask
import nl.pocketquest.server.api.entity.ResourceNode
import nl.pocketquest.server.api.entity.ResourceNodeSuppliedItem
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.api.item.ItemRoute
import nl.pocketquest.server.api.resource.ResourceInstance
import nl.pocketquest.server.api.resource.ResourceInventoryRoute
import nl.pocketquest.server.api.resource.ResourceTypeRoute
import nl.pocketquest.server.api.user.InventoryRoute
import nl.pocketquest.server.api.user.Status
import nl.pocketquest.server.api.user.StatusRoute
import nl.pocketquest.server.dataaccesslayer.DatabaseConfiguration
import nl.pocketquest.server.logic.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.logic.schedule.task.Task
import nl.pocketquest.server.testhelpers.MockDataSource
import nl.pocketquest.server.utils.getLogger
import org.junit.Assert.assertEquals

import org.junit.Before
import org.junit.Test

class ResourceGatheringRequestHandlerTest {

    private val playerStatus = MockDataSource(Status.IDLE.externalName)


    private val playerWood = MockDataSource(0L)
    private val treeWood = MockDataSource(0L)

    private val tree = object : ResourceInstance(Inventory(ResourceInventoryRoute("special_tree_23953").route), MockDataSource("tree")) {
        override suspend fun resourceNode() = ResourceNode(
                "tree_1", "Trees", "tree.png", "Tree", "1", "tree_empty.png",
                mapOf(
                        "wood_1" to ResourceNodeSuppliedItem("wood_1", 0, 6 to 7, 2)
                )
        )
    }

    @Before
    fun setup() {
        val tree: ResourceInstance = tree
        val playerInventory = Inventory(InventoryRoute("laurens").route)
        DatabaseConfiguration.testDB = TestDB(
                mapOf(
                        ResourceTypeRoute("special_tree_23953").route to MockDataSource("tree"),
                        StatusRoute("laurens").route to playerStatus,
                        ItemRoute(playerInventory, "wood_1").route to playerWood,
                        ItemRoute(tree.inventory, "wood_1").route to treeWood
                )
        )
        DatabaseConfiguration.test = true
    }

    @Test
    fun normal() {
        treeWood.content = 6
        executeResourceGathering()
        assertEquals(6L, playerWood.content)
        assertEquals(0L, treeWood.content!!)
    }

    @Test
    fun notWhileGathering() {
        treeWood.content = 6L
        playerWood.content = 0L
        playerStatus.content = Status.GATHERING.externalName
        executeResourceGathering()
        assertEquals(0L, playerWood.content!!)
        assertEquals(6L, treeWood.content!!)
    }

    private fun executeResourceGathering() {
        val taskWrapper = TaskWrapper()
        val handler = ResourceGatheringRequestHandler({ id ->
            tree.takeIf { id.equals("special_tree_23953") } ?: throw IllegalStateException("Can only resolve our test tree")
        }, taskWrapper::wrap)
        val request = ResourceGatheringRequest(
                "9873628",
                "laurens",
                "wood_1",
                System.currentTimeMillis(),
                "special_tree_23953"

        )
        runBlocking {
            val response = handler.handle(request, MockDataSource(request))
            taskWrapper.taskContents?.waitToFinish()
        }
    }

    private class TaskWrapper {
        var taskContents: TestableTask? = null
        fun wrap(task: Task): TestableTask {
            getLogger().info("Made a test task")
            val testableTask = TestableTask(task)
            taskContents = testableTask
            return taskContents!!
        }
    }
}
