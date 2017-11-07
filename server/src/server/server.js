// import * as scheduler from 'node-schedule'
import * as firebase from '../firebase/firebase'
import * as admin from 'firebase-admin'
import Item from '../entities/Item'
import ResourceNode from "../entities/ResourceNode";
import ResourceNodeFamily from "../entities/ResourceNodeFamily";

let initialized = false;
let entities = {};

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

        Server.loadEntities();
    }

    static loadEntities() {
        const db = admin.database();
        const entitiesRef = db.ref('/entities');

        entitiesRef.once('value', function(snapshot) {
            entities = Server.parseEntities(snapshot.val());

            console.log(entities);
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
            resource_nodes: ResourceNode.parse(
                data.resource_nodes,
                data.resource_node_supplied_items
            ),
            resource_node_families: ResourceNodeFamily.parse(
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
    }

}
