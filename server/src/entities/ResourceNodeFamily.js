/**
 * @property {string} id
 * @property {Array} gatheringToolTypes
 */
export default class ResourceNodeFamily {

    /**
     * @param {string} id
     * @param {Array} gatheringToolTypes
     */
    constructor(id, gatheringToolTypes) {
        this.id = id;
        this.gatheringToolTypes = gatheringToolTypes;
    }

    /**
     * @param {object} data The Firebase object, containing resource node family id's as keys and the resource node
     * family's properties as values
     * @return Object An object of ResourceNodeFamily objects
     */
    static parse(data) {
        const resourceNodeFamilies = {};

        for (let key of Object.keys(data)) {
            const value = data[key];

            resourceNodeFamilies[key] = ResourceNodeFamily.parseResourceNodeFamily(key, value);
        }

        return resourceNodeFamilies;
    }

    /**
     * @param {object} data
     * @param {string} key The resource node family's id
     * @param {object} data.gathering_tool_types
     */
    static parseResourceNodeFamily(key, data) {
        const gatheringToolTypes = [];

        for (let key of Object.keys(data.gathering_tool_types)) {
            gatheringToolTypes.push(key)
        }

        return new ResourceNodeFamily(
            key,
            gatheringToolTypes
        );
    }



}
