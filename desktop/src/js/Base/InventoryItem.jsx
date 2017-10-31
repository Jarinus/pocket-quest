import React from 'react';
import PropTypes from 'prop-types';

export default class InventoryItem extends React.Component {
    render() {
        const itemName = this.props.item.hasOwnProperty("name") ? this.props.item.name : "Unknown";

        return <div className="inventory-item">
            {itemName}
        </div>
    }
}

InventoryItem.propTypes = {
    item: PropTypes.object.isRequired
};
