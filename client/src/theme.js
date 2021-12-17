import {createTheme} from '@mui/material/styles';
// colors
import {green, grey, blue} from '@mui/material/colors';

// get default theme so we can use it's palette and so on in overrides
const theme = createTheme();

const appTheme = createTheme(theme, {
    palette: {
        primary: {
            main: green[500],
            light: green[100]
        },
        grey: {
            background: grey[100],
            text: grey[600]
        },
        blue: {
            link: blue[500],
            dark: blue[700]
        }
    },
    components: {
        MuiIconButton: {
            styleOverrides: {
                root: {
                    width: '40px',
                    height: '40px'
                }
            }
        },
        MuiButton: {
            styleOverrides: {
                contained: {
                    '&:hover': {
                        backgroundColor: green[600]
                    }
                }
            }
        }
    }
})

export default appTheme;
