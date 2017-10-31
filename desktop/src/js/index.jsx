import React from 'react';
import {render} from 'react-dom';
import Overview from './Base/Overview.jsx';

class App extends React.Component {
    render() {
        return <Overview/>;
    }
}

render(<App/>, document.getElementById('pocket-quest'));
