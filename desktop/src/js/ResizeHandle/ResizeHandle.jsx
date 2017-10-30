import React from "react";
import PropTypes from 'prop-types';

export default class ResizeHandle extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            resizing: false,
            resizeStart: {
                x: 0,
                y: 0
            },
            resizeCurrent: {
                x: 0,
                y: 0
            }
        }
    }

    componentWillMount() {
        window.addEventListener('mouseup', this.onMouseUp.bind(this));
        window.addEventListener('mousemove', this.onMouseMove.bind(this));
        window.addEventListener("mouseleave", this.onMouseLeave.bind(this));
    }

    componentWillUnmount() {
        window.removeEventListener('mouseup', this.onMouseUp.bind(this));
        window.removeEventListener('mousemove', this.onMouseMove.bind(this));
        window.removeEventListener("mouseleave", this.onMouseLeave.bind(this));
    }

    onMouseDown(event) {
        if (this.state.resizing) {
            return;
        }

        const state = this.state;
        state.resizing = true;
        state.resizeStart = state.resizeCurrent = {
            x: event.clientX,
            y: event.clientY
        };

        this.setState(state);
    }

    onMouseMove(event) {
        if (!this.state.resizing) {
            return;
        }

        const state = this.state;
        const xDifference = this.props.ignoreHorizontal ? 0 : event.clientX - state.resizeCurrent.x;
        const yDifference = this.props.ignoreVertical ? 0 : event.clientY - state.resizeCurrent.y;

        if (xDifference === 0&& yDifference === 0) {
            return;
        }

        state.resizeCurrent = {
            x: state.resizeCurrent.x + xDifference,
            y: state.resizeCurrent.y + yDifference
        };

        this.props.onResize({
            x: xDifference,
            y: yDifference
        });
        this.setState(state);
    }

    onMouseUp() {
        if (!this.state.resizing) {
            return;
        }

        this.endResize();
    }

    onMouseLeave(event) {
        event = event ? event : window.event;
        let from = event.relatedTarget || event.toElement;
        if (!from || from.nodeName === "HTML") {
            this.endResize();
        }
    }

    endResize() {
        const state = this.state;
        state.resizing = false;

        if (typeof this.props.onResizeDone === "function") {
            this.props.onResizeDone();
        }

        this.setState(state);
    }

    render() {
        return <div id={this.props.id}
                    style={this.props.style}
                    onMouseDown={this.onMouseDown.bind(this)}/>
    }
}

ResizeHandle.propTypes = {
    id: PropTypes.string,
    style: PropTypes.object,
    onResize: PropTypes.func.isRequired,
    onResizeDone: PropTypes.func,
    ignoreHorizontal: PropTypes.bool,
    ignoreVertical: PropTypes.bool
};
