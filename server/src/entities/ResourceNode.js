/**
 * @property {string} id
 * @property {string} icon
 * @property {string} family
 * @property {string} name
 * @property {number} tier
 * @property {object} suppliedItems
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
        this.id = id;
        this.icon = icon;
        this.family = family;
        this.name = name;
        this.tier = tier;
        this.suppliedItems = suppliedItems;
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

}
