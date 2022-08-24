import React, {useState} from 'react';
// mui
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
// mui icons
import EditIcon from '@mui/icons-material/Edit';
import {changeRole} from 'actions/actions';


export default function EditableField(props) {
    // userId for request 
    const {role, collaborationId} = props;
    const [isRoleFocused, setRoleFocused] = useState(false);
    const [renderedRole, setRenderedRole] = useState(role);

    const handleBlur = event => {
        if (!['EDITOR', 'READER'].includes(event.target.value)) {
            alert('The role must be EDITOR or READER');
        }
        else {
            changeRole(collaborationId, event.target.value).then(response => {
                if (response.status === 200) {
                    setRoleFocused(false);
                    alert('Changes will be applied after reload');
                    window.location.reload();
                }
                else if (response.status === 404) {
                    alert('Such collaboration does not exist!');
                }
                else {
                    alert('Something went wrong. Please, try again later!')
                }
            })
        }
    }

    return (
        <Box sx={{
            display: 'flex',
            alignItems: 'center'
        }}>
            {isRoleFocused
                ?
                <TextField
                    autoFocus
                    value={renderedRole}
                    onChange={event => setRenderedRole(event.target.value)}
                    onBlur={handleBlur} />
                :
                <Typography>{renderedRole}</Typography>
            }
            <IconButton sx={{
                width: '40px',
                height: '40px',
                transition: theme => theme.transitions.create(['color', 'background-color'], {duration: theme.transitions.duration.standard}),
            }}
                onClick={() => setRoleFocused(true)}>
                <EditIcon />
            </IconButton>
        </Box>
    )
}