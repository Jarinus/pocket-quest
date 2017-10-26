import React from 'react';
import InventoryOverlay from "./Inventory/InventoryOverlay.jsx";

const items = [
    'Axe',
    'Pickaxe',
    'Logs',
    'Planks',
    'Iron Ore',
    'Iron Bars',
    'Axe Handle',
    'Iron Axe Head'
];

export default class Overview extends React.Component {
    render() {
        return <div id="base-overview">
            <InventoryOverlay items={items}/>
        </div>;
    }
}
