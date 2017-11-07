/**
 * @property {string} id
 * @property {Array} gatheringToolTypes
 * @property {Array} members
 */
export default class ResourceNodeFamily {

    /**
     * @param {string} id
     * @param {Array} gatheringToolTypes
     * @param {Array} members
     */
    constructor(id, gatheringToolTypes, members) {
        this.id = id;
        this.gatheringToolTypes = gatheringToolTypes;
        this.members = members;
    }

    /**
     * @param {object} data The Firebase object, containing resource node family id's as keys and the resource node
     * family's properties as values
     * @param {object} familyMembers Firebase object, containing the family id as key and an object with family member
     * node id's as values
     * @return Object An object of ResourceNodeFamily objects
     */
    static parse(data, familyMembers) {
        const resourceNodeFamilies = {};

        for (let key of Object.keys(data)) {
            const value = data[key];
            const members = familyMembers[key];

            resourceNodeFamilies[key] = ResourceNodeFamily.parseResourceNodeFamily(key, value, members);
        }

        return resourceNodeFamilies;
    }

    /**
     * @param {object} data
     * @param {string} key The resource node family's id
     * @param {object} data.gathering_tool_types
     * @param {object} familyMembers
     */
    static parseResourceNodeFamily(key, data, familyMembers) {
        const gatheringToolTypes = [];
        const members = [];

        for (let key of Object.keys(data.gathering_tool_types)) {
            gatheringToolTypes.push(key)
        }

        for (let key of Object.keys(familyMembers)) {
            members.push(key)
        }

        return new ResourceNodeFamily(
            key,
            gatheringToolTypes,
            members
        );
    }
}
