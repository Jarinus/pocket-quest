import React from 'react';
import InventoryOverlay from "./InventoryOverlay.jsx";

export default class Overview extends React.Component {
    render() {
        return <div id="base-overview">
            <p>Hello World!</p>
            <InventoryOverlay inventory={['Axe', 'Pickaxe']}/>
        </div>;
    }
}
