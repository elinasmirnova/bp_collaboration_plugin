import React from 'react';
// mui
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'


export default function Home(props) {
    return (
        <Box sx={{
            color: 'primary.main',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100vh'
        }}>
            <Typography variant='h2'>
                Welcome to collaboration plugin!
            </Typography>
        </Box>
    )
};