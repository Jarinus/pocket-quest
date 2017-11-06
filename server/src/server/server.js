// import * as scheduler from 'node-schedule'
import * as firebase from '../firebase/firebase'
import * as admin from 'firebase-admin'
import Item from '../entities/Item.js'

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

            console.log(entities)
        });
    }

    /**
     * @param {object} data.items
     */
    static parseEntities(data) {
        return {
            items: Item.parse(data.items)
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
