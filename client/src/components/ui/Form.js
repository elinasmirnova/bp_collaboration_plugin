import React from 'react';
// lib
import {Form as FinalForm} from 'react-final-form';
// mui
import Box from '@mui/material/Box';


export default function Form(props) {
    const {children, disabled, formProps = {}, sx, onSubmit = () => { }, ...rest_of_props} = props;

    return (
        <FinalForm subscription={{}} {...rest_of_props}
            onSubmit={disabled ? () => { } : onSubmit}>
            {fFormProps => <Box
                sx={sx} {...formProps} component='form'
                onSubmit={event => {
                    event.preventDefault();
                    return fFormProps.handleSubmit(event);
                }}>
                {children && children(fFormProps)}
            </Box>}
        </FinalForm>
    )
};

