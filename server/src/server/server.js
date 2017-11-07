// import * as scheduler from 'node-schedule'
import * as firebase from '../firebase/firebase'
import * as admin from 'firebase-admin'
import Item from '../entities/Item'
import ResourceNode from "../entities/ResourceNode";
import ResourceNodeFamily from "../entities/ResourceNodeFamily";
import ResourceGatherRequestHandler from "../request/ResourceGatherRequestHandler";

let initialized = false;
let entities = {};
let resourceGatherRequestHandler;

export class Server {

    /**
     * Initializes the Game Server
     */
    static init() {
        if (initialized) {
            return;
        }

        initialized = true;

        firebase.init();

        Server.loadEntities(function (_entities) {
             entities = _entities;

             resourceGatherRequestHandler = new ResourceGatherRequestHandler(_entities);
        });
    }

    static loadEntities(entitiesCallback) {
        const db = admin.database();
        const entitiesRef = db.ref('/entities');

        entitiesRef.once('value', function(snapshot) {
            const _entities = Server.parseEntities(snapshot.val());
            entitiesCallback(_entities);
        });
    }

    /**
     * @param {object} data.items
     * @param {object} data.resource_nodes
     * @param {object} data.resource_node_supplied_items
     * @param {object} data.resource_node_families
     * @param {object} data.resource_node_resource_node_families
     */
    static parseEntities(data) {
        return {
            items: Item.parse(data.items),
            resourceNodes: ResourceNode.parse(
                data.resource_nodes,
                data.resource_node_supplied_items
            ),
            resourceNodeFamilies: ResourceNodeFamily.parse(
                data.resource_node_families,
                data.resource_node_resource_node_families
            )
        }
    }

    /**
     * Starts the Game Server
     * @throws Error when Game Server has not been initialized
     */
    static start() {
        if (!initialized) {
            throw new Error("Game Server must be initialized before use");
        }

        Server.listenToRequests();
    }

    static listenToRequests() {
        const db = admin.database();
        const requestsRef = db.ref("/requests");

        requestsRef.on('value', function (snapshot) {
            const data = snapshot.val();

            if (data === null) {
                return;
            }

            Server.handleRequests(data);

            snapshot.forEach(function (snapshotChild) {
                snapshotChild.ref.remove();
            })
        });
    }

    /**
     * @param {object} data
     * @param {object|null} data.resource_gathering
     */
    static handleRequests(data) {
        const resourceGatherRequests = data.resource_gathering;

        if (resourceGatherRequests) {
            resourceGatherRequestHandler.newRequests(resourceGatherRequests)
        }
    }

}
