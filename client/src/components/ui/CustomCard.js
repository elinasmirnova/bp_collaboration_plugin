import React from 'react';
// mui
import Card from '@mui/material/Card';


export default function CustomCard(props) {
    const {children, ...rest_of_props} = props;

    return <Card sx={{
        width: '320px',
        heigth: 'auto',
        px: 2,
        py: 4,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    }}
        {...rest_of_props}>
        {children}
    </Card>
};
