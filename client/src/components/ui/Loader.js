import React from 'react';
// mui
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';

export default function Loader() {
    return (
        <Box sx={{width: '100%', display: 'flex', justifyContent: 'center'}}>
            <CircularProgress />
        </Box>
    );
}
