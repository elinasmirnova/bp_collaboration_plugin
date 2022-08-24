import React, {useEffect, useState} from 'react';
// navigation
import {useNavigate} from 'react-router';
// data
import {roles} from 'utils/data'
// actions
import {getAllMindmaps, addCollaboration, addUser} from 'actions/actions';
// components
import Field from 'components/ui/Field';
import Form from 'components/ui/Form';
import Card from 'components/ui/CustomCard';
import Wrapper from 'components/ui/CustomWrapper';
// mui
import Box from '@mui/material/Box';
import CardContent from '@mui/material/CardContent'
import CardHeader from '@mui/material/CardHeader'
import Button from '@mui/material/Button'
import MenuItem from '@mui/material/MenuItem'


export default function AddCollaborator(props) {
    const {title, submitButtonLabel, isCreateUser} = props;
    const navigate = useNavigate();
    const [maps, setMaps] = useState([]);

    useEffect(() => {
        getAllMindmaps().then(response => setMaps(response))
    }, [])

    return (
        <Wrapper>
            <Card>
                <CardHeader title={title} />
                <CardContent>
                    <Form onSubmit={(values) => {
                        isCreateUser ?
                            addUser(values).then(response => {
                                if (response.status === 201) {
                                    navigate(-1);
                                } else if (response.status === 409) {
                                    alert('User with this e-mail already exists!');
                                }
                                else {
                                    alert('Something went wrong. Try again later!')
                                }
                            })
                            :
                            addCollaboration(values).then(response => {
                                if (response.status === 201) {
                                    navigate(-1);
                                } else if (response.status === 409) {
                                    alert('Such collaborator for this mindmap already exists!');
                                }
                                else {
                                    alert('Something went wrong. Try again later!')
                                }
                            });
                    }}>
                        {() => <Box sx={{
                            display: 'flex',
                            flexDirection: 'column'
                        }}>
                            {isCreateUser
                                ?
                                <Box sx={{
                                    display: 'flex',
                                    flexDirection: 'column'
                                }}>
                                    <Field name='first_name' label='First Name' required />
                                    <Field name='last_name' label='Last Name' required />
                                    <Field name='email' type='email' label={`Collaborator's email`} required />
                                    <Field name='password' type='password' label='Password' required />
                                </Box>
                                :
                                <Box sx={{
                                    display: 'flex',
                                    flexDirection: 'column'
                                }}>
                                    <Field name='map' type='select' label='Choose a map' required>
                                        {maps.map(item => {
                                            return <MenuItem key={item.mindmapId} value={item.mindmapId}>{item.title}</MenuItem>
                                        })}
                                    </Field>
                                    <Field name='email' type='email' label={`Collaborator's email`} required />
                                    <Field name='role' type='select' label='Choose a role' required>
                                        {roles.map((role, key) => {
                                            return <MenuItem key={key} value={role}>{role}</MenuItem>
                                        })}
                                    </Field>
                                </Box>
                            }
                            <Button variant='contained' type='submit'>{submitButtonLabel}</Button>
                            <Button onClick={() => navigate(-1)}>Return back</Button>
                        </Box>
                        }
                    </Form>
                </CardContent>
            </Card>
        </Wrapper>
    )
}