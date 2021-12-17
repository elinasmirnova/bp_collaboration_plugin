import React, {useState} from 'react';
// mui
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
// vectors
import ShowIcon from '@mui/icons-material/Visibility';
import HideIcon from '@mui/icons-material/VisibilityOff';


export default function Password(props) {
    const {InputProps = {}, ...rest_of_props} = props;
    // locale state to toggle visibility
    const [showPassword, setShowPassword] = useState(false);

    return <TextField
        InputProps={{
            ...InputProps,
            endAdornment: <InputAdornment position='end'>
                <IconButton size='large' onClick={() => setShowPassword(!showPassword)}>
                    {showPassword ? <HideIcon /> : <ShowIcon />}
                </IconButton>
            </InputAdornment>
        }}
        {...rest_of_props}
        type={showPassword ? 'text' : 'password'}
    />;
};
