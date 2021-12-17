import React from 'react';
// mui
import TextField from '@mui/material/TextField';


export default function Select(props) {
    const {value, onChange, ...rest_of_props} = props;

    return <TextField
            select
            value={value}
            onChange={onChange}
            {...rest_of_props} />       
};
