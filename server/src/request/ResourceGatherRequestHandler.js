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
    }

    newRequests(data) {
        for (let userId of Object.keys(data)) {
            const requestObjects = data[userId];

            for (let requestId of Object.keys(requestObjects)) {
                const requestProperties = requestObjects[requestId];
                const requestObject = this.parse(userId, requestId, requestProperties);

                this.handle(requestObject);
            }
        }
    }

    /**
     * @param {ResourceGatherRequest} request
     */
    handle(request) {
        const db = admin.database();
        const resourceNodeUIDsRef = db.ref('/resource_instances/' + request.resourceNodeUID);

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
        const db = admin.database();
        const resourceNodeInstanceRef = db.ref('/resource_instances/' + request.resourceNodeUID);
        const backpackRef = db.ref('/user_items/' + request.userId + '/backpack');
        const backpackReferenceHandler = (snapshot) => {
            const item = snapshot.child(itemId);
            let amount = 1;

            if (item.exists()) {
                amount += item.val();
            }

            item.ref.set(amount);
        };

        resourceNodeInstanceRef.once('value')
            .then((snapshot) => {
                const itemSnapshot = snapshot.child('resources_left').child(itemId)
                const itemsLeft = itemSnapshot.val();

                if (itemsLeft > 0) {
                    const itemSnapshot = snapshot.child('resources_left').child(itemId);
                    itemSnapshot.ref.set(itemSnapshot.val() - 1);

                    backpackRef.once('value')
                        .then(backpackReferenceHandler)
                } else {
                    console.error("too few items")
                }
            })
    }

    /**
     * @param {string} userId
     * @param {string} requestId
     * @param {object} properties
     * @param {string} properties.requested_at
     * @param {string} properties.resource_node_uid
     * @return ResourceGatherRequest
     */
    parse(userId, requestId, properties) {
        return new ResourceGatherRequest(
            requestId,
            userId,
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
