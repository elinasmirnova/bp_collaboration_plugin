import React from 'react';
// navidation
import {Link, useNavigate} from 'react-router-dom';
// mui
import Box from '@mui/material/Box';
import MenuList from '@mui/material/MenuList';
import MenuItem from '@mui/material/MenuItem';
// actions
import {logout} from 'actions/auth';
// img
import Logo from 'img/logo.png'


export default function Menu(props) {
    const {role} = props;
    const navigate = useNavigate();

    return (
        <Box sx={{
            position: 'fixed',
            left: 0,
            height: '100vh',
            boxShadow: 4,
            backgroundColor: 'common.white',
            width: 120
        }}>
            <Link to='/home'>
                <img src={Logo} alt='logo' style={{
                    width: '120px'
                }} />
            </Link>
            <MenuList>
                {role === 'ADMIN'
                    ?
                    <Box>
                        <MenuItem component={Link} to='/groups'>Groups</MenuItem>
                        <MenuItem component={Link} to='/users'>Users</MenuItem>
                    </Box>
                    :
                    <MenuItem component={Link} to='/mindmaps'>Mindmaps</MenuItem>
                }
                <MenuItem component={Link} to='/login' onClick={() => {
                    logout();
                    navigate('/')
                }}>Logout</MenuItem>
            </MenuList>
        </Box>
    )

};
