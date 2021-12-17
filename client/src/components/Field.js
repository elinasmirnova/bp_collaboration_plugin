import React, {useCallback, useMemo} from 'react';
// lib
import {Field as FinalField} from 'react-final-form';
// components
import PasswordField from 'components/fields/Password'
import Toggle from 'components/fields/Toggle';
import SelectField from 'components/fields/Select';
// mui
import TextField from '@mui/material/TextField';


export function PlainField(props) {
    const {label, type = 'text', disabled, required, helperText = null, ...rest_of_props} = props;

    const FieldComponent = useMemo(() => {
        switch (type) {
            case 'text':
            case 'email': {
                return TextField;
            }
            case 'password': {
                return PasswordField;
            }
            case 'select': {
                return SelectField;
            }
            case 'checkbox':
            case 'switch': {
                return Toggle;
            }
            default:
                throw new Error(`Not supported field type: ${type}`);
        }
    }, [type]);

    return <FieldComponent
        type={type} disabled={disabled} required={required}
        label={label} helperText={helperText ? helperText : ' '}
        {...rest_of_props}
    />;
};

export default function Field(props) {
    const {name, type, disabled, required, label, helperText, fFieldProps = {}, ...rest_of_props} = props;

    const validation = useCallback(value => {
        if (!value && required) {
            return 'Value is required';
        }
        if (type === 'email' && !value.includes('@')) {
            return 'Invalid email';
        }

    }, [type, required])

    return <FinalField
        {...fFieldProps}
        type={type}
        name={name}
        validate={value => validation(value)}>
        {fFieldProps => {
            // get submit or validation error
            const error = (!fFieldProps.meta.dirtySinceLastSubmit && fFieldProps.meta.submitError) ||
                (fFieldProps.meta.touched && fFieldProps.meta.error);

            return <PlainField
                {...fFieldProps.input}
                type={type} disabled={disabled} required={required} error={!!error}
                label={label} helperText={!!error ? error : helperText}
                {...rest_of_props}
            />
        }}
    </FinalField>
};
