import React, {useState, useEffect} from 'react';
// actions
import {deleteMap, getMindmaps} from 'actions/auth';
// components
import MapUsers from 'components/MapUsers';
// mui
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import IconButton from '@mui/material/IconButton';
// components
import Modal from 'components/CustomModal';
// mui icons
import MapInfoIcon from '@mui/icons-material/InfoOutlined';


export default function Mindmaps(props) {
    const [openLinkModal, setOpenLinkModal] = useState(false);
    const [renderedMaps, setRenderedMaps] = useState({'yourMindmaps': [], 'sharedMindmaps':[]});
    const [collapseId, setCollapseId] = useState(false);

    useEffect(() => {
        getMindmaps().then(mindmaps => setRenderedMaps(mindmaps))
    }, [])

    // delete mindmap
    const handleDelete = id => {
        deleteMap(id).then(response => {
            if (response.statusCode === 200) {
                setRenderedMaps(renderedMaps.filter(map => map.mindmapId !== id));
            }
            else {
                alert('Something went wrong. Please, try again later');
            }
        })
    }


    console.log(renderedMaps)
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

            </Box>
            <Box sx={{
                width: '100%'
            }}>
                <Typography variant='h5' sx={{width: '100%', textAlign: 'center', mb: 2}}>Your mindmaps:</Typography>
                {renderedMaps['yourMindmaps'].map(item => {
                    const usersOpen = collapseId === item.mindmapId;
                    return (
                        <Box key={item.mindmapId}>
                            <Card
                                sx={{
                                    my: 1,
                                    display: 'grid',
                                    gridTemplateColumns: 'repeat(4, 25%)',
                                    alignItems: 'center',
                                    px: 2
                                }}>
                                <CardHeader title={item.title} />
                                <IconButton onClick={() => setCollapseId(collapseId === item.mindmapId ? null : item.mindmapId)}>
                                    <MapInfoIcon />
                                </IconButton>
                                <Typography
                                    onClick={() => setCollapseId(collapseId === item.mindmapId ? null : item.mindmapId)}
                                    sx={{
                                        cursor: 'pointer',
                                        color: 'blue.link',
                                        width: 'fit-content',
                                        transition: theme => theme.transitions.create('color', {duration: theme.transitions.duration.standard}),
                                        '&:hover': {
                                            color: 'blue.dark'
                                        }
                                    }}>{`${item.collaborations.length} collaborators`}</Typography>
                                <Box sx={{display: 'flex', justifyContent: 'flex-end'}}>
                                    <Button variant='contained' onClick={() => setOpenLinkModal(true)} sx={{mr: 2}}>Share</Button>
                                    <Button color='error' onClick={() => handleDelete(item.mindmapId)}>Delete</Button>
                                </Box>
                            </Card>
                            {usersOpen && <MapUsers users={item.collaborations} creationDate={item.creationDate}
                                editionDate={item.editionDate} isPublic={item.public} mapId={item.mindmapId} isMine={true} />}
                        </Box>
                    )
                })}
                <Typography variant='h5' sx={{width: '100%', textAlign: 'center', mb: 2}}>Shared mindmaps:</Typography>
                {renderedMaps['sharedMindmaps'].map(item => {
                    const usersOpen = collapseId === item.mindmapId;
                    return (
                        <Box key={item.mindmapId}>
                            <Card
                                sx={{
                                    my: 1,
                                    display: 'grid',
                                    gridTemplateColumns: 'repeat(4, 25%)',
                                    alignItems: 'center',
                                    px: 2
                                }}>
                                <CardHeader title={item.title} />
                                <IconButton onClick={() => setCollapseId(collapseId === item.mindmapId ? null : item.mindmapId)}>
                                    <MapInfoIcon />
                                </IconButton>
                                <Typography
                                    onClick={() => setCollapseId(collapseId === item.mindmapId ? null : item.mindmapId)}
                                    sx={{
                                        cursor: 'pointer',
                                        color: 'blue.link',
                                        width: 'fit-content',
                                        transition: theme => theme.transitions.create('color', {duration: theme.transitions.duration.standard}),
                                        '&:hover': {
                                            color: 'blue.dark'
                                        }
                                    }}>{`${item.collaborations.length} collaborators`}</Typography>
                              
                            </Card>
                            {usersOpen && <MapUsers users={item.collaborations} creationDate={item.creationDate}
                                editionDate={item.editionDate} isPublic={undefined} mapId={item.mindmapId} isMine={false}/>}
                        </Box>
                    )
                })}
            </Box>
            <Modal open={openLinkModal} onClose={() => setOpenLinkModal(false)} />
        </Box>
    )
};
