/**
 * @property {string} id
 * @property {string} icon
 * @property {string} family
 * @property {string} name
 * @property {number} tier
 */
export default class ResourceNode {

    /**
     * @param {string} id
     * @param {string} icon
     * @param {string} family
     * @param {string} name
     * @param {number} tier
     */
    constructor(id, icon, family, name, tier) {
        this.id = id;
        this.icon = icon;
        this.family = family;
        this.name = name;
        this.tier = tier;
    }

    /**
     * @param {object} data The Firebase object, containing resource node id's as keys and the resource node's
     * properties as values
     * @return Array An array of ResourceNode objects
     */
    static parse(data) {
        const resourceNodes = [];

        for (let key of Object.keys(data)) {
            const value = data[key];

            const resourceNode = ResourceNode.parseResourceNode(key, value);
            resourceNodes.push(resourceNode);
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
     */
    static parseResourceNode(key, data) {
        return new ResourceNode(
            key,
            data.icon,
            data.family,
            data.name,
            data.tier
        );
    }

}
