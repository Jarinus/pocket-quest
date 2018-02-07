package nl.pocketquest.pocketquest.views.main.workorder

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout.VERTICAL
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.pocketquest.pocketquest.R
import nl.pocketquest.pocketquest.game.crafting.WorkOrder
import nl.pocketquest.pocketquest.game.crafting.WorkOrderStatus
import nl.pocketquest.pocketquest.utils.showOrHide
import nl.pocketquest.pocketquest.views.BaseFragment
import nl.pocketquest.pocketquest.views.main.workorder.WorkOrderFragment.WorkOrderAdapter.WorkOrderViewHolder.Companion.ROW_HEIGHT
import nl.pocketquest.pocketquest.views.main.workorder.WorkOrderFragment.WorkOrderAdapter.WorkOrderViewHolder.Companion.SPACE_BETWEEN_ELEMENTS
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import kotlin.math.roundToLong

private var serverOffset: Double = 0.0

class WorkOrderFragment : BaseFragment(), WorkOrderContract.WorkOrderView {

    /**
     * Note that this list is reversed (as a workaround for the RecyclerView centering, instead of
     * starting from the top).
     */
    private val workOrders: MutableList<WorkOrder> = mutableListOf()
    private val presenter = WorkOrderPresenter(this)
    private val mAdapter = WorkOrderAdapter(presenter, workOrders)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onAttach()
    }

    override fun setTimeOffset(offset: Double) {
        serverOffset = offset
    }

    override fun initialize(workOrders: Collection<WorkOrder>) {
        this.workOrders += workOrders
        this.workOrders.sortBy(WorkOrder::status)

        mAdapter.notifyItemRangeChanged(0, workOrders.size)
    }

    override fun setLoading(loading: Boolean) {
        view?.find<View>(R.id.workOrderLoadingSpinner)
                ?.showOrHide(loading)

        view?.find<View>(R.id.workOrderOverviewContainer)
                ?.showOrHide(!loading)


        info { "Loading: $loading" }
    }

    override fun addWorkOrder(workOrder: WorkOrder) {
        addWorkOrderOrdered(workOrder).also {
            mAdapter.notifyItemInserted(it)
        }
    }

    /**
     * Adds [workOrder] to [workOrders], returning its position
     * @return The position where [workOrder] was placed, or null
     */
    private fun addWorkOrderOrdered(workOrder: WorkOrder) = workOrders
            .indexOfFirst { workOrder.status < it.status }
            .let { maxOf(0, it) }
            .also { workOrders.add(it, workOrder) }

    override fun removeWorkOrder(workOrder: WorkOrder) {
        workOrders.indexOf(workOrder).also {
            workOrders.removeAt(it)

            mAdapter.notifyItemRemoved(it)
        }
    }

    override fun updateWorkOrder(workOrder: WorkOrder, newWorkOrder: WorkOrder) {
        workOrders.indexOf(workOrder).also {
            workOrders.removeAt(it)
            workOrders.add(it, newWorkOrder)

            mAdapter.notifyItemChanged(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            UI {
                verticalLayout {
                    relativeLayout {
                        id = R.id.workOrderLoadingSpinner
                        visibility = GONE

                        progressBar {
                            progressDrawable = CircularProgressDrawable(context)
                        }.lparams {
                            width = dip(96)
                            height = dip(96)
                            centerInParent()
                        }

                        lparams {
                            width = matchParent
                            height = matchParent
                        }
                    }

                    recyclerView {
                        lparams {
                            height = matchParent
                            width = matchParent
                        }

                        id = R.id.workOrderOverviewContainer
                        layoutManager = LinearLayoutManager(context, VERTICAL, false)
                        adapter = mAdapter

                        lparams {
                            height = matchParent
                        }
                    }
                }
            }.view

    private class WorkOrderAdapter(
            private val presenter: WorkOrderPresenter,
            private val workOrders: List<WorkOrder>
    ) : RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): WorkOrderViewHolder =
                with(parent!!.context) {
                    verticalLayout {
                        textView {
                            id = R.id.workOrderTitle
                            textSize = 20f
                        }

                        linearLayout {
                            relativeLayout {
                                imageView {
                                    id = R.id.workOrderIcon
                                    imageResource = R.drawable.knight // TODO: Replace
                                }

                                textView {
                                    id = R.id.workOrderCount
                                    textColor = WHITE
                                    typeface = Typeface.DEFAULT_BOLD

                                    setShadowLayer(4f, 0f, 0f, BLACK)
                                }.lparams {
                                    alignParentBottom()
                                    alignParentRight()
                                }

                                lparams {
                                    width = dip(ROW_HEIGHT)
                                    height = dip(ROW_HEIGHT)
                                }
                            }

                            relativeLayout {
                                horizontalProgressBar {
                                    id = R.id.workOrderProgress
                                }.lparams {
                                    width = matchParent
                                    centerInParent()
                                }
                            }.lparams {
                                weight = 1f
                                height = matchParent
                                leftMargin = dip(SPACE_BETWEEN_ELEMENTS)
                                rightMargin = dip(SPACE_BETWEEN_ELEMENTS)
                            }

                            button {
                                id = R.id.workOrderActionButton
                            }.lparams {
                                height = dip(ROW_HEIGHT)
                            }

                            lparams {
                                width = matchParent
                                height = dip(ROW_HEIGHT)
                            }
                        }

                        lparams {
                            padding = dip(SPACE_BETWEEN_ELEMENTS)
                            width = matchParent
                        }
                    }
                }.let(::WorkOrderViewHolder)

        override fun getItemCount(): Int = workOrders.count()

        override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int) {
            holder.update(presenter, workOrders[position])
        }

        private class WorkOrderViewHolder(view: View) : RecyclerView.ViewHolder(view), AnkoLogger {
            val workOrderTitle: TextView = view.find(R.id.workOrderTitle)
            val workOrderIcon: ImageView = view.find(R.id.workOrderIcon)
            val workOrderCount: TextView = view.find(R.id.workOrderCount)
            val workOrderProgress: ProgressBar = view.find(R.id.workOrderProgress)
            val workOrderActionButton: Button = view.find(R.id.workOrderActionButton)

            fun update(presenter: WorkOrderPresenter, workOrder: WorkOrder) {
                workOrderTitle.text = "WorkOrder" //TODO: Get first acquired item's name and put here
                //TODO: Uncomment and implement
//                workOrderIcon.imageBitmap = null
                workOrderCount.text = workOrder.count.toString()
                workOrder.status.also {
                    when (it) {
                        is WorkOrderStatus.Finished ->
                            handleFinishedWorkOrder(it, presenter, workOrder)
                        is WorkOrderStatus.Active ->
                            handleActiveWorkOrder(it, presenter, workOrder)
                        is WorkOrderStatus.Submitted ->
                            handleSubmittedWorkOrder(it, presenter, workOrder)
                    }
                }
            }

            fun handleFinishedWorkOrder(finished: WorkOrderStatus.Finished, presenter: WorkOrderPresenter, workOrder: WorkOrder) {
                workOrderProgress.progress = 1
                workOrderProgress.max = 1
                workOrderActionButton.textResource = R.string.work_order_claim_button
                workOrderActionButton.onClick {
                    wtf("claim $workOrder")
                    presenter.onClaimWorkOrder(workOrder)
                }
            }

            fun handleActiveWorkOrder(active: WorkOrderStatus.Active, presenter: WorkOrderPresenter, workOrder: WorkOrder) {
                val duration = (active.finishesAt - active.startedAt) / 100 //400
                wtf("max: $duration")
                workOrderProgress.max = duration.toInt()

                fun executePerMilisecond(lambda: suspend () -> Boolean) {
                    async(UI) {
                        while (isActive) {
                            if (lambda()) return@async
                            else delay(100)
                        }
                    }
                }
                executePerMilisecond {
                    val serverTime = (System.currentTimeMillis() - serverOffset).roundToLong()
                    val elapsedTime = (serverTime - active.startedAt) / 100
                    val progress = minOf(duration, elapsedTime)
                    wtf("duration:$duration startedAt: ${active.startedAt}; finishedAt: ${active.finishesAt}; serverTime: $serverTime; elapsedTime: $elapsedTime; progress: $progress")
                    workOrderProgress.progress = progress.toInt()
                    progress == duration
                }

                workOrderActionButton.textResource = R.string.work_order_cancel_button
                workOrderActionButton.onClick {
                    wtf("cancel $workOrder")
                    presenter.onCancelWorkOrder(workOrder)
                }
            }

            fun handleSubmittedWorkOrder(submitted: WorkOrderStatus.Submitted, presenter: WorkOrderPresenter, workOrder: WorkOrder) {
                workOrderProgress.progress = 0
                workOrderProgress.max = 1

                workOrderActionButton.textResource = R.string.work_order_cancel_button
                workOrderActionButton.onClick {
                    wtf("cancel $workOrder")
                    presenter.onCancelWorkOrder(workOrder)
                }
            }

            companion object {
                const val SPACE_BETWEEN_ELEMENTS = 12
                const val ROW_HEIGHT = 48
            }
        }
    }
}
