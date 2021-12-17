import React from 'react';
import ReactDOM from 'react-dom';
// navigation 
import {BrowserRouter as Router} from 'react-router-dom';
// components
import App from 'components/App';
// styles
import {ThemeProvider} from '@mui/material/styles';
import theme from 'theme';


ReactDOM.render(
    <ThemeProvider theme={theme}>
        <Router>
            <App />
        </Router>
    </ThemeProvider>,
    document.getElementById('root')
);
