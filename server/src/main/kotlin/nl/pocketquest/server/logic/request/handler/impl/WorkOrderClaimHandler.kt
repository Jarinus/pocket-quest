package nl.pocketquest.server.logic.request.handler.impl

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventPool
import nl.pocketquest.server.logic.request.handler.RequestHandler
import nl.pocketquest.server.logic.request.handler.Response
import nl.pocketquest.server.logic.request.impl.CraftingCancelRequest
import nl.pocketquest.server.logic.request.impl.CraftingClaimRequest
import nl.pocketquest.server.logic.schedule.crafting.WorkOrderData
import nl.pocketquest.server.logic.schedule.crafting.WorkorderStatus


class WorkOrderClaimHandler(private val kodein: Kodein) : RequestHandler<CraftingClaimRequest> {

    /**
     * Fires WORKORDER_CLAIMED event
     * else returns error code 400
     */
    suspend override fun handle(request: CraftingClaimRequest, requestReference: DataSource<CraftingClaimRequest>): Response {
        kodein.instance<EventPool>().submit(
                Event.of(
                        WorkorderStatus.WORK_ORDER_CLAIMED,
                        WorkOrderData(request.workorder_id, request.workorder_id),
                        request.submitted_at
                )
        )
        return Response(null, 200)
    }

    override val route = Findable.Companion.of(listOf("requests", "crafting_claim"), CraftingClaimRequest::class.java)
}