import React, {useEffect, useState} from 'react';
// navigation
import {useNavigate} from 'react-router';
// data
// import {maps} from 'utils/mockData'
// components
import MapUsers from 'components/MapUsers';
// mui
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import IconButton from '@mui/material/IconButton';
// mui icons
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { getAllMindmaps } from 'actions/auth';


export default function Groups(props) {
    const navigate = useNavigate();
    const [collapseId, setCollapseId] = useState(false);
    const [maps, setMaps] =useState([]);

    useEffect(() => {
        getAllMindmaps().then(response => setMaps(response))
    }, [])

    return (
        <Box sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'flex-start',
            height: '100vh'
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
                    Groups
            </Typography>
                <Button variant='contained' onClick={() => navigate('/addCollaborator')}>
                    Add a collaborator
            </Button>
            </Box>
            <Box sx={{
                width: '100%'
            }}>
                {maps.map(item => {
                    const usersOpen = collapseId === item.mindmapId;
                    return (
                        <Box key={item.mindmapId}>
                            <Card
                                onClick={() => setCollapseId(collapseId === item.mindmapId ? null : item.mindmapId)}
                                sx={{
                                    cursor: 'pointer',
                                    my: 1,
                                    display: 'grid',
                                    gridTemplateColumns: '48% 48% 4%',
                                    alignItems: 'center'
                                }}>
                                <CardHeader title={item.title} />
                                <Typography sx={{
                                    color: 'blue.link',
                                    width: 'fit-content',
                                    transition: theme => theme.transitions.create('color', {duration: theme.transitions.duration.standard}),
                                    '&:hover': {
                                        color: 'blue.dark'
                                    }
                                }}>{`${item.collaborations.length} collaborators`}
                                </Typography>
                                <IconButton sx={{
                                    transition: theme => theme.transitions.create('background-color', {duration: theme.transitions.duration.standard}),

                                }}>
                                    {usersOpen ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                                </IconButton>
                            </Card>
                            {usersOpen && <MapUsers users={item.collaborations} isMine={true} />}
                        </Box>
                    )
                })}
            </Box>
        </Box>
    )
};