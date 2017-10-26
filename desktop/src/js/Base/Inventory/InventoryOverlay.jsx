import React from 'react';
import PropTypes from 'prop-types';
import InventoryItem from "./InventoryItem.jsx";
import ArrayUtils from '../../Util/ArrayUtils.jsx';
import InventoryRow from "./InventoryRow.jsx";

export const itemSize = 48;
export const padding = 12;

const initialWidth = 360;

export default class InventoryOverlay extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            width: initialWidth
        }
    }

    render() {
        const styleProperty = Object.assign({width: this.state.width}, style.overlay);

        return <div id="inventory-overlay"
                    style={styleProperty}>
            <h2>Inventory</h2>
            <div id="inventory-container"
                 style={style.container}>
                {this.renderInventoryItems()}
            </div>
        </div>
    }

    renderInventoryItems() {
        const itemsPerRow = InventoryItem.getItemsPerRow(this.state.width);
        const splitItemsArray = ArrayUtils.chunk(this.props.items, itemsPerRow);

        return splitItemsArray.map(function (items, index) {
            const key = "inventory-row-" + index;

            return <InventoryRow
                key={key}
                index={index}
                items={items}/>
        })
    }
}

InventoryOverlay.propTypes = {
    items: PropTypes.array.isRequired
};

const style = {
    overlay: {
        position: 'absolute',
        top: 0,
        bottom: 0,
        left: 0
    },
    container: {
        display: 'flex',
        flexDirection: 'column',
        padding: padding
    }
};
