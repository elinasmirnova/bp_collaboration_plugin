import React from 'react';
import Box from '@mui/material/Box';

//TODO: minHeight and custom margin padding (disable scroll when using wrapper)
export default function CustomWrapper(props) {
    const {children, ...rest_of_props} = props;

    return <Box sx={{
        height: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    }}
        {...rest_of_props}>
        {children}
    </Box>;
}
