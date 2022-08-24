import React, {useState} from 'react';
// components 
import EditableField from 'components/ui/EditableField';
// mui
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import Checkbox from '@mui/material/Checkbox';
import IconButton from '@mui/material/IconButton';
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
// mui icons
import DeleteIcon from '@mui/icons-material/Delete';
import {changeMapVisibility, deleteCollaboration} from 'actions/actions';


export default function MapUsers(props) {
    const {users, creationDate, editionDate, isPublic, mapId, isMine, onChangePublic} = props;
    const [renderedUsers, setRenderedUsers] = useState(users);
    const role = localStorage.getItem('role');

    // delete user from your mindmap
    const handleDelete = id => {
        deleteCollaboration(id).then(response => {
            if (response.status === 204) {
                setRenderedUsers(renderedUsers.filter(user => user.collaborationId !== id));
                alert('Changes will be applied after reload');
                window.location.reload();
            }
            else {
                alert('Something went wrong. Please, try again later');
            }
        })
    }

    // change map's public status 
    const handleChange = (event) => {
        changeMapVisibility(mapId, event.target.checked).then(response => {
            if (response.status === 200) {
                onChangePublic(event, mapId)
            }
            else {
                alert('Something went wrong. Please, try again later!')
            }
        })
    }

    return (
        <Box sx={{
            width: '100%'
        }}>
            {role !== 'ADMIN' && <Box sx={{
                display: 'flex',
                justifyContent: 'space-evenly',
                alignItems: 'center',
                color: 'grey.text',
                my: 2

            }}>
                <Typography>{`Creation date: ${creationDate}`}</Typography>
                <Typography>{`Edition date: ${editionDate}`}</Typography>
                {isMine && <FormGroup>
                    <FormControlLabel control={<Checkbox checked={isPublic} onChange={handleChange} />} label="Public" />
                </FormGroup>}
            </Box>}
            <Box>
                {renderedUsers.map((user, key) => {

                    return <Box key={user.collaboratorId} sx={{
                        px: 2
                    }}>
                        <Box sx={{
                            my: 1,
                            p: 2,
                            alignItems: 'center',
                            display: 'flex',
                            justifyContent: isMine ? 'space-between' : 'space-evenly'

                        }}>
                            <Typography>{user.collaboratorEmail}</Typography>
                            {isMine
                                ?
                                <Box sx={{
                                    display: 'flex',
                                    width: '50%',
                                    justifyContent: 'space-between'
                                }}>
                                    <EditableField role={user.role} collaborationId={user.collaborationId} />
                                    <IconButton
                                        onClick={() => handleDelete(user.collaborationId)}
                                        sx={{
                                            width: '40px',
                                            height: '40px',
                                            transition: theme => theme.transitions.create(['color', 'background-color'], {duration: theme.transitions.duration.standard}),
                                            '&:hover': {
                                                color: 'error.main',
                                                backgroundColor: 'rgba(211, 47, 47, 0.04)'
                                            }
                                        }}>
                                        <DeleteIcon />
                                    </IconButton>
                                </Box>
                                : <Typography>{user.role}</Typography>}

                        </Box>
                        {(key !== renderedUsers.length - 1) && <Divider light />}
                    </Box>
                })}
            </Box>
        </Box>
    )
}