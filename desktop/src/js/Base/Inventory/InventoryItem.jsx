import React from 'react';
import PropTypes from 'prop-types';

const itemSize = 48;
const padding = 12;

export default class InventoryItem extends React.Component {
    static getNumberOfItemsPerRow(width) {
        if (!Number.isInteger(width)) {
            throw "Non-integer value: " + width;
        }

        return Math.floor((width + padding) / (itemSize + padding));
    }

    render() {
        let styleProperty = Object.assign({}, style.item);

        if (this.props.index === 0) {
            styleProperty = Object.assign(styleProperty, style.itemFirst)
        }

        return <div className="inventory-item"
                    style={styleProperty}
                    data-name={this.props.name}/>
    }
}

InventoryItem.propTypes = {
    index: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired
};

const style = {
    item: {
        width: itemSize,
        height: itemSize,
        overflow: 'hidden',
        backgroundColor: 'gray',

        marginLeft: padding
    },
    itemFirst: {
        marginLeft: 0
    }
};
