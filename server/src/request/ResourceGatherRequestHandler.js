import * as admin from 'firebase-admin'

const scheduler = require('node-schedule');

/**
 * @property {object} items
 * @property {object} resourceNodes
 * @property {object} resourceNodeFamilies
 */
export default class ResourceGatherRequestHandler {

    /**
     * @param {object} items
     * @param {object} resourceNodes
     * @param {object} resourceNodeFamilies
     */
    constructor({items, resourceNodes, resourceNodeFamilies}) {
        this.items = items;
        this.resourceNodes = resourceNodes;
        this.resourceNodeFamilies = resourceNodeFamilies;
        this.database = admin.database();
    }

    handle(snapshot) {
        const key = snapshot.ref.key;
        const properties = snapshot.val();

        const requestObject = ResourceGatherRequestHandler.parse(key, properties);

        this.database.ref(`/users/${requestObject.userId}/status`)
            .transaction((currentStatus) => {
                if (currentStatus === null) {
                    return currentStatus;
                }

                if (currentStatus !== "gathering") {
                    return "gathering"
                }
            }, (_, committed) => {
                if (committed) {
                    this.processRequest(requestObject);
                }
            });

        snapshot.ref.remove();
    }

    /**
     * @param {ResourceGatherRequest} request
     */
    processRequest(request) {
        const resourceNodeUIDsRef = this.database.ref('/resource_instances/' + request.resourceNodeUID);

        resourceNodeUIDsRef.once('value', (snapshot) => {
            if (!snapshot.exists()) {
                console.error("Invalid Resource Node UID: " + request.resourceNodeUID);
            }

            const {resources_left, type} = snapshot.val();
            const resourceNode = this.resourceNodes[type];
            this.scheduleGathering(request, resources_left, resourceNode)
        })
    }

    /**
     * @param {ResourceGatherRequest} request
     * @param {object} resourcesLeft
     * @param {ResourceNode} resourceNode
     */
    scheduleGathering(request, resourcesLeft, resourceNode) {
        for (let itemId of Object.keys(resourcesLeft)) {
            const itemAmount = resourcesLeft[itemId];
            const durationPerItem = resourceNode.suppliedItems[itemId].duration;

            for (let i = 1; i <= itemAmount; i++) {
                const scheduleDuration = (durationPerItem * i) * 1000;
                const scheduleDate = new Date(new Date().getTime() + scheduleDuration);

                scheduler.scheduleJob(scheduleDate, () => {
                    this.processGathering(request, itemId);
                })
            }
        }
    }

    /**
     * @param {ResourceGatherRequest} request
     * @param {string} itemId
     */
    processGathering(request, itemId) {
        const resourceRef = this.database.ref(`/resource_instances/${request.resourceNodeUID}/resources_left/${itemId}`);
        const backpackRef = this.database.ref(`/user_items/${request.userId}/backpack/${itemId}`);

        resourceRef.transaction((value) => {
            if (value !== null) {
                value -= 1
            }

            if (value < 0) {
                return
            }

            return value
        }, (_, committed, snapshot) => {
            if (committed) {
                backpackRef.transaction((current) => {
                    return (current || 0 ) + 1
                });

                if (snapshot.val() === 0) {
                    this.setUserStatus(request.userId, "idle");
                }
            }
        })
    }

    setUserStatus(userId, status) {
        this.database
            .ref(`/users/${userId}/status`)
            .transaction((currentValue) => {
                if (currentValue === null) {
                    return currentValue;
                }

                if (currentValue !== status) {
                    return status;
                }
            });
    }

    /**
     * @param {string} requestId
     * @param {object} properties
     * @param {string} properties.user_id
     * @param {string} properties.requested_at
     * @param {string} properties.resource_node_uid
     * @return ResourceGatherRequest
     */
    static parse(requestId, properties) {
        return new ResourceGatherRequest(
            requestId,
            properties.user_id,
            properties.requested_at,
            properties.resource_node_uid
        );
    }

}

/**
 * @property {string} id
 * @property {string} userId
 * @property {string} requestedAt
 * @property {string} resourceNodeUID
 */
class ResourceGatherRequest {

    /**
     * @param {string} id
     * @param {string} userId
     * @param {string} requestedAt
     * @param {string} resourceNodeUID
     */
    constructor(id, userId, requestedAt, resourceNodeUID) {
        this.id = id;
        this.userId = userId;
        this.requestedAt = requestedAt;
        this.resourceNodeUID = resourceNodeUID;
    }

}
