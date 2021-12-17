import React, {useMemo} from 'react';
// mui
import Checkbox from '@mui/material/Checkbox';
import Switch from '@mui/material/Switch';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';


export default function Toggle(props) {
    const {type = 'checkbox', label = '', helperText, disabled, required, error,
        labelPlacement = 'end', ...rest_of_props} = props;

    // get specific component based on type
    const ToggleComponent = useMemo(() => {
        switch (type) {
            case 'checkbox':
                return Checkbox;
            case 'switch':
                return Switch;
            default:
                throw new Error(`Not supported toggle type: ${type}`);
        }
    }, [type]);

    return <FormControl disabled={disabled} required={required} error={error}>
        <FormControlLabel
            control={<ToggleComponent {...rest_of_props} />}
            label={label}
            labelPlacement={labelPlacement}
        />
        {helperText && <FormHelperText>
            {helperText}
        </FormHelperText>}
    </FormControl>;
};
