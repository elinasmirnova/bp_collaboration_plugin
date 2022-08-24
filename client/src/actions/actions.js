import {request} from 'utils/request';

// POST register
export const signup = async values => {
    const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            email: values.email,
            password: values.password,
            firstName: values.firstName,
            lastName: values.lastName,
            isAdmin: false
        })
    };

    try {
        const response = await request('http://localhost:8080/auth/register', requestOptions);
        return response;
    }
    catch (error) {
        return error;
    }
};

// POST login
export const login = async values => {
    if (!values.checkbox) {
        // logout in 10 minutes if remember me is not checked
        setTimeout(() => {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            alert('Your session expired, log in again');
            window.location.reload();
        }, 3600000)
    }
    const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({email: values.email, password: values.password})
    };

    try {
        const response = await request('http://localhost:8080/auth/login', requestOptions);
        return response;
    }
    catch (error) {
        return error;
    }
};


// GET user's mindmaps 
export const getMindmaps = async () => {
    const token = localStorage.getItem("token");
    const requestOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${token}`
        }
    };

    const response = await request(`/mindmaps`, requestOptions
    ).then(data => {
        return data;
    })
    return await response;
}


// GET all mindmaps
export const getAllMindmaps = async () => {
    const token = localStorage.getItem("token");
    const requestOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${token}`
        },
    };

    const response = await request(`/mindmaps/all`, requestOptions
    ).then(data => {
        return data;
    })
    return await response;
}

// GET all users
export const getAllUsers = async () => {
    const token = localStorage.getItem("token");
    const requestOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${token}`
        },
    };

    const response = await request(`/users`, requestOptions
    ).then(data => {
        return data;
    })
    return await response
}


// PUT change role for a collaboration on the mindmap
export const changeRole = async (collaborationId, newRole) => {
    const token = localStorage.getItem("token");
    const url = `/collaborations/${collaborationId}?role=${newRole}`;
    const requestOptions = {
        method: "PUT",
        headers: {
            Authorization: `${token}`,
            "Content-Type": "application/json",
        },
        body: null
    };

    try {
        const response = await request(url, requestOptions);
        return response;
    } catch (error) {
        return Promise.reject(error);
    }
}

// PUT map isPublic
export const changeMapVisibility = async (mapId, isPublic) => {
    const token = localStorage.getItem("token");
    const url = `/mindmaps/${mapId}?isPublic=${isPublic}`;
    const requestOptions = {
        method: "PUT",
        headers: {
            Authorization: `${token}`,
            "Content-Type": "application/json",
        },
        body: null
    };
    try {
        const response = await request(url, requestOptions);
        return response;
    } catch (error) {
        return Promise.reject(error);
    }
}

//DELETE
export const deleteMap = async mapId => {
    const token = localStorage.getItem("token");
    const url = `/mindmaps/${mapId}`;
    const requestOptions = {
        method: "DELETE",
        headers: {
            Authorization: `${token}`,
        },
        body: null
    };

    try {
        return await request(url, requestOptions);
    } catch (error) {
        return Promise.reject(error);
    }
};


// DELETE collaboration
export const deleteCollaboration = async collaborationId => {
    const token = localStorage.getItem("token");
    const url = `/collaborations/${collaborationId}`;
    const requestOptions = {
        method: "DELETE",
        headers: {
            Authorization: `${token}`,
        },
        body: null
    };

    try {
        return await request(url, requestOptions);
    } catch (error) {
        return Promise.reject(error);
    }
};

// DELETE user
export const deleteUser = async collaboratorId => {
    const token = localStorage.getItem("token");
    const url = `/users/${collaboratorId}`;
    const requestOptions = {
        method: "DELETE",
        headers: {
            Authorization: `${token}`,
        },
        body: null
    };

    try {
        return await request(url, requestOptions);
    } catch (error) {
        return Promise.reject(error);
    }
};

// POST add new collabation for mindmap
export const addCollaboration = async (values) => {
    const token = localStorage.getItem("token");
    const url = `/collaborations`;
    const requestOptions = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            Authorization: `${token}`
        },
        body: JSON.stringify({
            collaboratorEmail: values.email,
            mindmapId: values.map,
            role: values.role
        })
    }

    try {
        return await request(url, requestOptions);
    } catch (error) {
        return error;
    }
};

// POST create a new user
export const addUser = async (values) => {
    const token = localStorage.getItem("token");
    const url = `/users`;
    const requestOptions = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            Authorization: `${token}`,
        },
        body: JSON.stringify({
            email: values.email,
            password: values.password,
            firstName: values.first_name,
            lastName: values.last_name
        })
    }

    try {
        return await request(url, requestOptions);
    } catch (error) {
        return error;
    }
};

export const generateLink = async (values, map_id) => {
    const token = localStorage.getItem("token");
    const url = `/mindmaps/${map_id}/share?email=${values.email}&role=${values.role}`;
    const requestOptions = {
        method: 'GET',
        headers: {
            'Authorization': `${token}`,
            'Content-Type': 'application/json'
        },
        body: null
    }

    try {
        return await fetch(url, requestOptions)
    }
    catch (error) {
        return Promise.reject(error);
    }
};


export const logout = async () => {
    const token = localStorage.getItem("token");
    const requestOptions = {
        method: 'POST',
        headers: {
            Authorization: `${token}`
        },
        body: null
    };

    try {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        return await request(`/logout`, requestOptions)
    } catch (error) {
        return Promise.reject(error);
    }
};
