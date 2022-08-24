import React, {useState, useEffect} from 'react';
// actions
import {deleteMap, getMindmaps} from 'actions/actions';
// components
import MapUsers from 'components/ui/MapUsers';
import Loader from 'components/ui/Loader';
import Modal from 'components/ui/CustomModal';
// mui
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import IconButton from '@mui/material/IconButton';
// mui icons
import MapInfoIcon from '@mui/icons-material/InfoOutlined';


export default function Mindmaps(props) {
    const [openLinkModal, setOpenLinkModal] = useState(false);
    const [renderedMaps, setRenderedMaps] = useState({'yourMindmaps': [], 'sharedMindmaps': []});
    const [collapseId, setCollapseId] = useState(false);
    const [isLoading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        getMindmaps().then(mindmaps => {
            setLoading(false);
            setRenderedMaps(mindmaps)
        })
    }, [])

    // delete mindmap
    const handleDelete = id => {
        deleteMap(id).then(response => {
            if (response.status === 204) {
                setRenderedMaps({...renderedMaps, 
                    yourMindmaps: renderedMaps.yourMindmaps.filter(map => map.mindmapId !== id)});
            }
            else {
                alert('Something went wrong. Please, try again later');
            }
        })
    }

    const formatDate = timestamp => {
        const d = new Date(timestamp);
        const date = d.toLocaleDateString() + " " + d.getUTCHours() + ":" + d.getUTCMinutes();
        return date;
    }

    const onChangePublic = (event, mapId) => {
        console.log({checked: event.target.checked, mapId})
        const {yourMindmaps, sharedMindmaps} = renderedMaps;
        const currentMindmap = yourMindmaps.find(map => map.mindmapId === mapId);
        const currentMindmapIndex = yourMindmaps.findIndex(map => map.mindmapId === mapId);
        const newMindmap = {...currentMindmap, public: !currentMindmap.public};
        const newMindmaps = [...yourMindmaps];
        newMindmaps[currentMindmapIndex] = newMindmap;
        setRenderedMaps({
            sharedMindmaps,
            yourMindmaps: newMindmaps
        });
    }


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
                {isLoading && <Loader />}
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
                            {usersOpen && <MapUsers users={item.collaborations} creationDate={formatDate(item.creationDate)}
                                editionDate={formatDate(item.editionDate)} mapId={item.mindmapId} isMine={true}
                                onChangePublic={onChangePublic}
                            />}
                            <Modal open={openLinkModal} onClose={() => setOpenLinkModal(false)} mindmapId={item.mindmapId} />
                        </Box>

                    )
                })}
                <Typography variant='h5' sx={{width: '100%', textAlign: 'center', mb: 2, mt: 8}}>Shared mindmaps:</Typography>
                {isLoading && <Loader />}
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
                            {usersOpen && <MapUsers users={item.collaborations} creationDate={formatDate(item.creationDate)}
                                editionDate={formatDate(item.editionDate)} isPublic={undefined} mapId={item.mindmapId} isMine={false} />}
                        </Box>
                    )
                })}
            </Box>
        </Box>
    )
};
