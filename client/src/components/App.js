import React, {useEffect, useState} from 'react';
// navigation
import {Routes, Route, useLocation, Navigate} from "react-router-dom";
// components
import Home from 'components/modules/Home'
import Menu from 'components/ui/Menu'
import Groups from 'components/modules/Groups';
import Users from 'components/modules/Users';
import Mindmaps from 'components/modules/Mindmaps';
import Login from 'components/modules/Login';
import Signup from 'components/modules/Signup';
import AddCollaborator from 'components/modules/AddCollaborator';
import NotFound from 'components/modules/NotFound';
// mui
import Box from '@mui/material/Box';


function App() {
    const location = useLocation();
    const [isAuth, setAuth] = useState(null);
    const [role, setRole] = useState(() => localStorage.getItem('role'));

    useEffect(() => {
        setAuth(localStorage.getItem('token') !== null);
        setRole(localStorage.getItem('role'));
    }, [location.pathname])

    return (
        isAuth
            ?
            <Box sx={{
                backgroundColor: 'grey.background',
                minHeight: '100vh'
            }}>
                <Menu role={role} />
                <Box sx={{
                    pt: 4,
                    px: 10,
                    ml: 10,
                    minHeight: '100vh'
                }}>
                    {role === 'ADMIN'
                        ?
                        <Routes>
                            <Route path='/' element={<Navigate to='/home' />} />
                            <Route path='/login' element={<Navigate to='/home' />} />
                            <Route path='/home' element={<Home />} />
                            <Route path='/groups' element={<Groups />} />
                            <Route path='/users' element={<Users />} />
                            <Route path='/mindmaps' element={<Mindmaps />} />
                            <Route path='/addCollaborator' element={<AddCollaborator title='Add a new collaborator' submitButtonLabel='Add' isCreateUser={false} />} />
                            <Route path='/createUser' element={<AddCollaborator title='Create a new user' submitButtonLabel='Create' isCreateUser={true} />} />
                            <Route path='*' element={<NotFound />} />
                        </Routes>
                        :
                        <Routes>
                            <Route path='/' element={<Navigate to='/home' />} />
                            <Route path='/login' element={<Navigate to='/home' />} />
                            <Route path='/home' element={<Home />} />
                            <Route path='/mindmaps' element={<Mindmaps />} />
                            <Route path='*' element={<NotFound />} />
                        </Routes>
                    }
                </Box>
            </Box>
            :
            <Box sx={{
                backgroundColor: 'grey.background',
                minHeight: '100vh'
            }}>
                <Routes>
                    <Route path='/' element={<Navigate to='/login' />} />
                    {!role && <Route path='*' element={<Navigate to='/login' />} />}
                    <Route path='/login' element={<Login />} />
                    <Route path='/signup' element={<Signup />} />
                </Routes>
            </Box>
    )
}

export default App;
