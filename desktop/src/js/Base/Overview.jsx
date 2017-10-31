import React from 'react';
import InventoryOverlay from "./Inventory/InventoryOverlay.jsx";
import background from '../../../assets/images/background.png';
import townHall from '../../../assets/images/town-hall.png';

const path = require('path');

const townHallWidth = 564;
const townHallHeight = 409;

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
    constructor(props) {
        super(props);

        this.state = {
            width: window.innerWidth,
            height: window.innerHeight
        }
    }

    componentDidMount() {
        window.addEventListener("resize", this.updateDimensions.bind(this));
    }

    componentWillUnmount() {
        window.removeEventListener("resize", this.updateDimensions.bind(this));
    }

    updateDimensions() {
        const state = this.state;
        state.width = window.innerWidth;
        state.height = window.innerHeight;
        this.setState(state);
    }

    renderTownHall() {
        const townHallUrl = path.join('js', townHall);
        const topOffset = (this.state.height - townHallHeight) / 2;
        const leftOffset = (this.state.width - townHallWidth) / 2;
        const style = {
            position: 'absolute',
            top: topOffset,
            left: leftOffset
        };

        return <img src={townHallUrl} style={style} draggable={false}/>
    }

    render() {
        const backgroundUrl = path.join('js', background);
        const fillFullScreenStyleProperty = {
            width: this.state.width,
            height: this.state.height
        };

        return <div id="base-overview" style={{position: 'relative'}}>
            <img src={backgroundUrl} style={fillFullScreenStyleProperty} draggable={false}/>
            {this.renderTownHall()}
            <InventoryOverlay items={items}
                              minItemsPerRow={4}
                              maxItemsPerRow={8}/>
        </div>;
    }
}
