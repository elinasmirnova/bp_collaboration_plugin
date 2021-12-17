import React, {useEffect, useState} from 'react';
// navigation
import {useNavigate} from 'react-router';
// actions
import { deleteUser, getAllUsers } from 'actions/auth';
// mui
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import IconButton from '@mui/material/IconButton';
// mui icons
import DeleteIcon from '@mui/icons-material/Delete';


export default function Users(props) {
    const navigate = useNavigate();
    const [renderedUsers, setRenderedUsers] = useState([]);

    useEffect(() => {
        getAllUsers().then(response => setRenderedUsers(response))
    }, [])

    const handleDelete = id => {
        deleteUser(id).then(response => {
            if (response.status === 204){
                setRenderedUsers(renderedUsers.filter(user => user.collaboratorId !== id));
            }
            else { 
                alert('Something went wrong. Please, try again later');
            } 
        })
    }

    return (
        <Box sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-start',
            justifyContent: 'center',
        }}>
            <Box sx={{
                width: '100%',
                display: 'grid',
                gridTemplateColumns: '80% 20%',
                alignItems: 'center',
                mb: 4
            }}>
                <Typography variant='h4' sx={{
                    textAlign: 'center'
                }}>
                    Users
            </Typography>
                <Button variant='contained' onClick={() => navigate('/createUser')}>
                    Create a new user
            </Button>
            </Box>
            <Box sx={{
                width: '100%'
            }}>
                {renderedUsers.map(user => {
                    return <Card key={user.collaboratorId} sx={{
                        my: 1,
                        p: 2,
                        alignItems: 'center',
                        display: 'grid',
                        gridTemplateColumns: '48% 48% 4%'
                    }}>
                        <Typography>{`${user.firstName} ${user.lastName}`}</Typography>
                        <Typography>{user.email}</Typography>
                        <IconButton
                            onClick={() => handleDelete(user.collaboratorId)}
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
                    </Card>
                })}
            </Box>
        </Box>
    )
};
