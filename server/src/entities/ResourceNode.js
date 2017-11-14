/**
 * @property {string} _id
 * @property {string} _icon
 * @property {string} _family
 * @property {string} _name
 * @property {number} _tier
 * @property {object} _suppliedItems
 */
export default class ResourceNode {

    /**
     * @param {string} id
     * @param {string} icon
     * @param {string} family
     * @param {string} name
     * @param {number} tier
     * @param {object} suppliedItems
     */
    constructor(id, icon, family, name, tier, suppliedItems) {
        this._id = id;
        this._icon = icon;
        this._family = family;
        this._name = name;
        this._tier = tier;
        this._suppliedItems = suppliedItems;
    }

    /**
     * @returns {string}
     */
    get id() {
        return this._id
    }

    /**
     * @returns {string}
     */
    get icon() {
        return this._icon
    }

    /**
     * @returns {string}
     */
    get family() {
        return this._family
    }

    /**
     * @returns {string}
     */
    get name() {
        return this._name;
    }

    /**
     * @returns {number}
     */
    get tier() {
        return this._tier;
    }

    /**
     * @returns {Object}
     */
    get suppliedItems() {
        return this._suppliedItems;
    }

    /**
     * @param {object} data The Firebase object, containing resource node id's as keys and the resource node's
     * properties as values
     * @param {object} resourceNodeSuppliedItems Firebase object, containing item id's as keys and the supplied item's properties
     * as values
     * @return Object An object of ResourceNode objects
     */
    static parse(data, resourceNodeSuppliedItems) {
        const resourceNodes = {};

        for (let key of Object.keys(data)) {
            const value = data[key];
            const suppliedItems = resourceNodeSuppliedItems[key];

            resourceNodes[key] = ResourceNode.parseResourceNode(key, value, suppliedItems);
        }

        return resourceNodes;
    }

    /**
     * @param {object} data
     * @param {string} key The item's id
     * @param {string} data.icon
     * @param {string} data.family
     * @param {string} data.name
     * @param {number} data.tier
     * @param {object} suppliedItems
     */
    static parseResourceNode(key, data, suppliedItems) {
        return new ResourceNode(
            key,
            data.icon,
            data.family,
            data.name,
            data.tier,
            suppliedItems
        );
    }

    parse(data, resourceNodeSuppliedItems) {
        const resourceNodes = {};

        for (let key of Object.keys(data)) {
            const value = data[key];
            const suppliedItems = resourceNodeSuppliedItems[key];

            resourceNodes[key] = ResourceNode.parseResourceNode(key, value, suppliedItems);
        }

        return resourceNodes;
    }

}
