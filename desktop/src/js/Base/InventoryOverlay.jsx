import React from 'react';
import PropTypes from 'prop-types';
import InventoryItem from "./InventoryItem.jsx";

export default class InventoryOverlay extends React.Component {
    render() {
        return <div id="inventory-overlay">
            <h2>Inventory</h2>
            <div id="inventory-container">
                {this.renderInventoryItems()}
            </div>
        </div>
    }

    renderInventoryItems() {
        return this.props.inventory.map(function (item, index) {
            const key = "inventory-item-" + index;
            const itemProp = {
                name: item
            };

            return <InventoryItem key={key} item={itemProp}/>
        });
    }
}

InventoryOverlay.propTypes = {
    inventory: PropTypes.array.isRequired
};
