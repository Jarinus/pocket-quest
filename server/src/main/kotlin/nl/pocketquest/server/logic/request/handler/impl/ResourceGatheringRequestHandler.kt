package nl.pocketquest.server.logic.request.handler.impl

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.api.entity.Item
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.logic.request.handler.RequestHandler
import nl.pocketquest.server.logic.request.handler.Response
import nl.pocketquest.server.logic.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.api.resource.ResourceInstance
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.api.user.Status
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.api.user.updateUser
import nl.pocketquest.server.logic.Tier
import nl.pocketquest.server.logic.TierInterval
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringData
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringStatus
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventPool

class ResourceRequestRoute : Findable<ResourceGatheringRequest> {
    override val route = listOf("requests", "resource_gathering")
    override val expectedType = ResourceGatheringRequest::class.java
}

class ResourceGatheringRequestHandler internal constructor(
        private val kodein: Kodein
) : RequestHandler<ResourceGatheringRequest> {

    private val eventPool: EventPool = kodein.instance()

    suspend override fun handle(request: ResourceGatheringRequest, requestReference: DataSource<ResourceGatheringRequest>): Response {
        val resourceNode = ResourceInstance.byId(request.resource_node_uid, kodein).resourceNode()
        //Haal de family op
        val family = resourceNode?.family ?: return Response("404 family not found", 404)
        //Haal alle gathering tool types op
        val gatheringToolType = kodein.instance<Entities>().resourceNodeFamily(family)?.gatheringToolTypes
        //Haal alle entities op
        val entities = kodein.instance<Entities>()
        //Haal alle op deze node bruikbare tool types op
        val allUsableTools = gatheringToolType?.flatMap {
            entities.gatheringToolFamily(it)?.members ?: emptySet()
        } ?: emptySet<String>()
        //Haal de huidige user op
        val inventory = User.byId(request.user_id, kodein).inventory
        //Check of er een tools in de inventory zit met een tier die hoog genoeg en het hoogst is

        val bestTool = allUsableTools.asSequence()
                .mapNotNull { entities.item(it) }
                .filter { Tier(it.tier) >= Tier(resourceNode.tier) }
                .sortedByDescending { Tier(it.tier) }
                .firstOrNull { inventory.item(it.name).count() > 0 }
                ?: return Response("User does not have the right tool", 403)

        val interval = TierInterval().calcInterval(bestTool.tier, resourceNode.tier,resourceNode
                ?.suppliedItems
                ?.get(request.resource_id)
                ?.duration
                ?: return Response("Invalid request", 400))




        updateUser(request.user_id, kodein) {
            if (setStatus(Status.GATHERING)) {
                eventPool.submit(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                        request.user_id,
                        request.resource_node_uid,
                        request.resource_id,
                        request.tool_id,
                        interval
                ), request.requested_at))
            }
        }
        return Response(null, 200)
    }

    override val route = ResourceRequestRoute()
}
