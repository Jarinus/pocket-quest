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
import nl.pocketquest.server.logic.request.impl.CraftingStartRequest
import nl.pocketquest.server.logic.schedule.crafting.WorkOrderCreationData
import nl.pocketquest.server.logic.schedule.crafting.WorkorderStatus

class WorkOrderRequestStartHandler(private val kodein: Kodein) : RequestHandler<CraftingStartRequest> {

    suspend override fun handle(request: CraftingStartRequest, requestReference: DataSource<CraftingStartRequest>): Response {
        kodein.instance<EventPool>()
                .submit(
                        Event.of(WorkorderStatus.WORK_ORDER_CREATED,
                                WorkOrderCreationData(request.user_id, request.recipe_id, request.count),
                                request.submitted_at
                        )
                )
        return Response(null, 200)
    }

    override val route = Findable.Companion.of(listOf("requests", "crafting_start"), CraftingStartRequest::class.java)
}