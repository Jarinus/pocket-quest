import React from 'react';
import PropTypes from 'prop-types';
import InventoryItem from "./InventoryItem.jsx";
import ArrayUtils from '../../Util/ArrayUtils.jsx';
import InventoryRow from "./InventoryRow.jsx";
import ResizeHandle from "../../ResizeHandle/ResizeHandle.jsx";

export const itemSize = 48;
export const padding = 12;

const resizeHandleWidth = 4;

export default class InventoryOverlay extends React.Component {
    constructor(props) {
        super(props);

        if (this.props.maxItemsPerRow < this.props.minItemsPerRow) {
            this.props.maxItemsPerRow = this.props.minItemsPerRow;
        }

        this.state = {
            width: InventoryItem.getWidthForNumberOfItemsPerRow(this.props.minItemsPerRow),
            minWidth: InventoryItem.getWidthForNumberOfItemsPerRow(this.props.minItemsPerRow),
            maxWidth: InventoryItem.getWidthForNumberOfItemsPerRow(this.props.maxItemsPerRow) + resizeHandleWidth
        }
    }

    snapToClosestNumberOfItems(actualWidth) {
        const numberOfItemsPerRow = InventoryItem.getNumberOfItemsPerRow(actualWidth);
        const calculatedLowerSnapWidth = InventoryItem.getWidthForNumberOfItemsPerRow(numberOfItemsPerRow);
        const calculatedUpperSnapWidth = InventoryItem.getWidthForNumberOfItemsPerRow(numberOfItemsPerRow + 1);

        const lowerSnapWidth = calculatedLowerSnapWidth < this.state.minWidth
            ? this.state.minWidth
            : calculatedLowerSnapWidth;
        const upperSnapWidth = calculatedUpperSnapWidth > this.state.maxWidth
            ? this.state.maxWidth
            : calculatedUpperSnapWidth;

        if (actualWidth - lowerSnapWidth < upperSnapWidth - actualWidth) {
            return lowerSnapWidth;
        } else {
            return upperSnapWidth;
        }
    }

    onResize(relativePosition) {
        const state = this.state;
        const newWidth = state.width + relativePosition.x;

        if (newWidth < this.state.minWidth
            || newWidth >= this.state.maxWidth
            || newWidth > (window.innerWidth - 4)) {
            return;
        }

        state.width = newWidth;
        this.setState(state);
    }

    onResizeDone() {
        const state = this.state;
        state.width = this.snapToClosestNumberOfItems(state.width);
        this.setState(state);
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
            <ResizeHandle id="inventory-overlay-resize-handle"
                          style={style.handle}
                          onResize={this.onResize.bind(this)}
                          onResizeDone={this.onResizeDone.bind(this)}/>
        </div>
    }

    renderInventoryItems() {
        const itemsPerRow = InventoryItem.getNumberOfItemsPerRow(this.state.width);
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
    items: PropTypes.array.isRequired,
    minItemsPerRow: PropTypes.number.isRequired,
    maxItemsPerRow: PropTypes.number.isRequired
};

const style = {
    overlay: {
        position: 'absolute',
        top: 0,
        bottom: 0,
        left: 0,
        userSelect: 'none'
    },
    container: {
        display: 'flex',
        flexDirection: 'column',
        padding: padding
    },
    handle: {
        position: 'absolute',
        top: 0,
        bottom: 0,
        right: -resizeHandleWidth,
        width: resizeHandleWidth,
        background: 'lightblue',
        cursor: 'col-resize'
    }
};
