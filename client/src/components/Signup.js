import React from 'react';
// actions
import {signup} from 'actions/auth';
// navigation 
import {useNavigate} from 'react-router-dom';
// components
import Form from 'components/Form';
import Field from 'components/Field';
import Wrapper from 'components/CustomWrapper';
import Card from 'components/CustomCard';
// mui
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import Button from '@mui/material/Button';


export default function Signup(props) {
    const navigate = useNavigate();

    return (
        <Wrapper>
            <Form onSubmit={values =>  {
               signup(values).then(response => {
                console.log(response)
                if (response.status === 201) {
                    navigate('/login');
                }
                else if (response.statusCode === 409) {
                    alert('Such user exists!');
                }
                else {
                    alert('Something went wrong. Try again later!')
                }
               })
            }}>
                {() => <Card>
                    <CardHeader title='Signup' subheader='Sign in to the Freeplain application' sx={{textAlign: 'center', mb: 2}} />
                    <CardContent sx={{
                        display: 'flex',
                        flexDirection: 'column'
                    }}>
                        <Field name='firstName' label='First Name' required />
                        <Field name='lastName' label='Last Name' required />
                        <Field name='email' label='Email' type='email' required />
                        <Field name='password' label='Password' type='password' required />
                        <Button variant='contained' type='submit' sx={{mb: 1}}>Sign up</Button>
                        <Button onClick={() => navigate('/login')}>I already have the account</Button>
                    </CardContent>
                </Card>}
            </Form>
        </Wrapper>
    )
};
