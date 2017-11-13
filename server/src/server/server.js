// import * as scheduler from 'node-schedule'
import * as firebase from '../firebase/firebase'
import * as admin from 'firebase-admin'
import Item from '../entities/Item'
import ResourceNode from '../entities/ResourceNode';
import ResourceNodeFamily from '../entities/ResourceNodeFamily';
import ResourceGatherRequestHandler from '../request/ResourceGatherRequestHandler';

let entities = {};
let resourceGatherRequestHandler;

export class Server {

    /**
     * Initializes the Game Server
     */
    static start() {
        firebase.init();

        Server.loadEntities((_entities) => {
            entities = _entities;

            resourceGatherRequestHandler = new ResourceGatherRequestHandler(_entities);

            Server.listenToRequests();
        });
    }

    static loadEntities(entitiesCallback) {
        const db = admin.database();
        const entitiesRef = db.ref('/entities');

        entitiesRef.once('value', (snapshot) => {
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

    static listenToRequests() {
        const db = admin.database();

        db.ref('/requests/resource_gathering')
            .on('child_added', resourceGatherRequestHandler.handle.bind(resourceGatherRequestHandler))
    }

}
