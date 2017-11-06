/**
 * @property {string} id
 * @property {string} icon
 * @property {string} name
 * @property {number} tier
 */
export default class Item {

    /**
     * @param {string} id
     * @param {string} icon
     * @param {string} name
     * @param {number} tier
     */
    constructor(id, icon, name, tier) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.tier = tier;
    }

    /**
     * @param {object} data The Firebase object, containing item id's as keys and the item's properties as values
     * @return Object An object of Item objects
     */
    static parse(data) {
        const items = {};

        for (let key of Object.keys(data)) {
            const value = data[key];

            items[key] = Item.parseItem(key, value);
        }

        return items;
    }

    /**
     * @param {object} data
     * @param {string} key The item's id
     * @param {string} data.icon
     * @param {string} data.name
     * @param {number} data.tier
     */
    static parseItem(key, data) {
        return new Item(
            key,
            data.icon,
            data.name,
            data.tier
        );
    }

}
