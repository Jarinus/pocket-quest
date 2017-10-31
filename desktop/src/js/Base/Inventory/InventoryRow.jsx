import React from 'react';
import PropTypes from 'prop-types';
import InventoryItem from "./InventoryItem.jsx";

export default class InventoryRow extends React.Component {
    render() {
        let styleProperty = Object.assign({}, style.row);

        if (this.props.index === 0) {
            styleProperty = Object.assign(styleProperty, style.rowFirst)
        }

        return <div style={styleProperty}>
            {this.renderInventoryItems()}
        </div>
    }

    renderInventoryItems() {
        return this.props.items.map(function (itemName, index) {
            const key = "inventory-item-" + index;

            return <InventoryItem
                key={key}
                index={index}
                name={itemName}/>
        });
    }
}

InventoryRow.propTypes = {
    index: PropTypes.number.isRequired,
    items: PropTypes.array.isRequired
};

const style = {
    row: {
        display: 'flex',
        marginTop: 12
    },
    rowFirst: {
        marginTop: 0
    }
};
