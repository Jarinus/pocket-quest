package nl.pocketquest.server.task.impl

import com.google.firebase.database.*
import nl.pocketquest.server.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.task.Task
import java.util.concurrent.TimeUnit

class ResourceGatheringTask(
        interval: Long,
        timeUnit: TimeUnit,
        private val request: ResourceGatheringRequest
) : Task(interval, timeUnit, true) {
    override fun validate(): Boolean {
        return true
    }

    override fun execute() {
        FirebaseDatabase.getInstance()
                .getReference("/resource_instances/${request.resource_node_uid}/resources_left/${request.resource_id}")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val currentValue = mutableData.getValue(Int::class.java)

                        if (currentValue != null) {
                            if (currentValue <= 0) {
                                return Transaction.abort()
                            }

                            mutableData.value = currentValue - 1
                        }

                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                        if (committed) {
                            updateBackpack()
                        } else {
                            //TODO: Set user status to "idle"
                            scheduleNext = false
                        }
                    }
                })
    }

    private fun updateBackpack() {
        FirebaseDatabase.getInstance()
                .getReference("/user_items/${request.user_id}/backpack/${request.resource_id}")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val currentValue = mutableData.getValue(Int::class.java)

                        if (currentValue == null) {
                            mutableData.value = 1
                        } else {
                            mutableData.value = currentValue + 1
                        }

                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                        if (!committed) {
                            kotlin.error("Unable update backpack: ${request.resource_id} x1:\n$error")
                        }
                    }
                })
    }

}
