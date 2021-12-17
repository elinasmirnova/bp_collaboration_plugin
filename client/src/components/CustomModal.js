import React, {useState} from 'react';
// data
import {roles} from 'utils/mockData';
// mui 
import Modal from '@mui/material/Modal';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import MenuItem from '@mui/material/MenuItem';
import IconButton from '@mui/material/IconButton';
import Alert from '@mui/material/Alert';
// components
import Field from 'components/Field';
import Form from 'components/Form';
// mui icons
import CopyLinkIcon from '@mui/icons-material/ContentCopy';
import CloseIcon from '@mui/icons-material/Close';


export default function CustomModal(props) {
    const {open, onClose} = props;
    const [showLink, setShowLink] = useState(false);
    const [link, setLink] = useState('test link');
    const [alert, setAlert] = useState(false);

    return <Modal open={open} onClose={onClose} sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    }}>
        <Box sx={{
            width: '40%', height: '50%',
            backgroundColor: 'common.white',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            position: 'relative'
        }}>
            <IconButton onClick={onClose}
                sx={{position: 'absolute', m: 1, right: 0, top: 0}}><CloseIcon /></IconButton>
            <Box sx={{
                height: '70%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-start',
                flexDirection: 'column'
            }}>
                <Typography variant='h5' sx={{mb: 4}}>Generate link</Typography>
                {showLink
                    ? <Box sx={{
                        width: '160px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        py: 2, px: 2,
                        border: '1px solid #80808026',
                        borderRadius: 1,
                    }}>
                        <Typography sx={{mr: 2}}>{link}</Typography>
                        <IconButton onClick={() => {
                            navigator.clipboard.writeText(link);
                            setAlert(true);
                            setTimeout(() => {
                                // After 3 seconds set the show value to false
                                setAlert(false)
                            }, 2000);
                        }
                        }><CopyLinkIcon /></IconButton>
                    </Box>
                    :
                    <Form onSubmit={() => setShowLink(true)}>
                        {() => <Box sx={{
                            display: 'flex',
                            alignItems: 'center',
                        }}>
                            <Field name='email' type='email' label='Email' required />
                            <Field name='role' type='select' label='Choose a role' required sx={{
                                mx: 4, minWidth: '160px'
                            }}>
                                {roles.map((role, key) => {
                                    return <MenuItem key={key} value={role}>{role}</MenuItem>
                                })}
                            </Field>
                            <Button type='submit' variant='contained' sx={{mb: 2}}>Generate</Button>

                        </Box>}
                    </Form>}
                {alert && <Alert severity='success' sx={{mt: 2, px: 2}}>Copied!</Alert>}
            </Box>
        </Box>
    </Modal>;
}

