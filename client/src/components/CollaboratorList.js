import React, {useState} from 'react';
// mui
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';


export default function CollaboratorList(props) {
    const {collaborators} = props;

    const [customCollaborators, setCustomCollaborators] = useState(collaborators);

    const handleDelete = id => {
        setCustomCollaborators(customCollaborators.filter(collaborator => collaborator.collaboratorId !== id))
    };

    return (
        <Box>
            {customCollaborators.map(collaborator => {
                return <Chip key={collaborator.collaboratorId} label={collaborator.collaboratorEmail} sx={{m: 0.5}} onDelete={() => handleDelete(collaborator.collaboratorId)} />
            })}
        </Box>
    );
}
