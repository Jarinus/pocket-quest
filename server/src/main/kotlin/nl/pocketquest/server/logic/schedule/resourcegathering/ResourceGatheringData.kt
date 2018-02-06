package nl.pocketquest.server.logic.schedule.resourcegathering

data class ResourceGatheringData(
        val userID: String,
        val resourceInstanceId: String,
        val resourceID: String,
        val toolID: String,
        val interval: Int
)

enum class ResourceGatheringStatus {
    STARTED_GATHERING,
    GAINS_RESOURCE
}