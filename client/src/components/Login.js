import React from 'react';
// actions
import {login} from 'actions/auth';
// navigation 
import {useNavigate} from 'react-router-dom';
// components
import Field from 'components/Field';
import Form from 'components/Form';
import Wrapper from 'components/CustomWrapper';
import Card from 'components/CustomCard';
// mui
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import Button from '@mui/material/Button';


export default function Login(props) {
    const navigate = useNavigate();

    return (
        <Wrapper>
            <Form onSubmit={values => {
                // login(values);
                // navigate('/home')
                login(values).then(async response =>{
                    if (response.status === 200) {
                        const data = await response.json();
                        localStorage.setItem('role', data.userRole);
                        localStorage.setItem('token', data.token);
                        navigate('/home');
                    }
                    else {
                        alert('Something went wrong');
                    }
                });

            }}>
                {() => <Card>
                    <CardHeader title='Login' subheader='Log in to the Freeplain application' sx={{textAlign: 'center', mb: 2}} />
                    <CardContent sx={{
                        display: 'flex',
                        flexDirection: 'column'
                    }}>
                        <Field name='email' label='Email' type='email' required />
                        <Field name='password' label='Password' type='password' required />
                        <Field name='checkbox' label='Remember me' type='checkbox' />
                        <Button variant='contained' type='submit' sx={{mb: 1}}>Login</Button>
                        <Button onClick={() => navigate('/signup')}>I'm a new user</Button>
                    </CardContent>
                </Card>
                }
            </Form>
        </Wrapper>
    )
};
