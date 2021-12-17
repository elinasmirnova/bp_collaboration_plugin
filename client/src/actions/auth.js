import {request} from 'utils/request';

// POST register
export const signup = async values => {
    values.isAdmin = false;
    const url = 'http://localhost:8080/auth/register';
    try {
        const response = await request(
            url,
            "POST",
            {
                email: values.email,
                password: values.password,
                firstName: values.firstName,
                lastName: values.lastName,
                isAdmin: values.isAdmin
            },
            {
                "Content-Type": "application/json",
            }
        );
        return response;
    } catch (error) {
        return error;
    }
};

// POST login
export const login = async values => {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({  email: values.email, password: values.password })
    };

    const response = await fetch('/auth/login', requestOptions);
    return response;
};

// GET user's mindmaps 
export const getMindmaps = async () => {
    const token = localStorage.getItem("token");
    const response = await fetch(`/mindmaps`, {
            method: 'GET',
            cache: 'no-cache',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `${token}`
            },
            redirect: 'follow'
        }
    ).then((data) => {
        return data.json()
    })
    return await response
}


// GET all mindmaps
export const getAllMindmaps = async () => {
    const token = localStorage.getItem("token");
    const response = await fetch(`/mindmaps/all`, {
            method: 'GET',
            cache: 'no-cache',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `${token}`
            },
            redirect: 'follow'
        }
    ).then((data) => {
        return data.json()
    })
    return await response
}

// GET all users
export const getAllUsers = async () => {
    const token = localStorage.getItem("token");
    const response = await fetch(`/users`, {
            method: 'GET',
            cache: 'no-cache',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `${token}`
            },
            redirect: 'follow'
        }
    ).then((data) => {
        return data.json()
    })
    return await response
}


// PUT change role for a collaboration on the mindmap
export const changeRole = async (collaborationId, newRole) => {
    const token = localStorage.getItem("token");
    const url = `/collaborations/${collaborationId}?role=${newRole}`;
    try {
        const response = await request(
            url,
            "PUT",
            null,
            {
                Authorization: `${token}`,
                "Content-Type": "application/json",
            }
        );
        return response;
    } catch (error) {
        return Promise.reject(error);
    }
}

// PUT map isPublic
export const changeMapVisibility = async (mapId, isPublic) => {
    const token = localStorage.getItem("token");
    const url = `/mindmaps/:${mapId}?isPublic=${isPublic}`;
    try {
        const response = await request(
            url,
            "PUT",
            null,
            {
                Authorization: `Basic ${token}`,
                "Content-Type": "application/json",
            }
        );
        return response;
    } catch (error) {
        return Promise.reject(error);
    }
}

//DELETE
export const deleteMap = async mapId => {
    const token = localStorage.getItem("token");
    const url = `http://localhost:8080/mindmaps/${mapId}`;

    try {
        return await request(url, "DELETE", null, {
            Authorization: `${token}`,
        });
    } catch (error) {
        return Promise.reject(error);
    }
};

// DELETE collaboration
export const deleteCollaboration = async collaborationId => {
    const token = localStorage.getItem("token");
    const url = `/collaborations/${collaborationId}`;

    try {
        return await request(url, "DELETE", null, {
            Authorization: `${token}`,
        });
    } catch (error) {
        return Promise.reject(error);
    }
};

// DELETE user
export const deleteUser = async collaboratorId => {
    const token = localStorage.getItem("token");
    const url = `/users/${collaboratorId}`;

    try {
        return await request(url, "DELETE", null, {
            Authorization: `${token}`,
        });
    } catch (error) {
        return Promise.reject(error);
    }
};

// POST add new collabation for mindmap
export const addCollaboration = async (values) => {
    const token = localStorage.getItem("token");
    const url = `/collaborations`;

    try {
        return await request(url, "POST",
        {
            collaboratorEmail: values.collaboratorEmail,
            mindmapId: values.mapId,
            role: values.role
        }, 
        {
            Authorization: `${token}`,
        });
    } catch (error) {
        return Promise.reject(error);
    }
};

// POST create a new user
export const addUser = async (values) => {
    const token = localStorage.getItem("token");
    const url = `/users`;

    try {
        return await request(url, "POST",
        {
            email: values.collaboratorEmail,
            password: values.password,
            firstName: values.firstName,
            lastName: values.lastName
        }, 
        {
            Authorization: `${token}`,
        });
    } catch (error) {
        return Promise.reject(error);
    }
};

export const logout = async () => {
    const token = localStorage.getItem("token");

    try {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        return await request(`/logout`, "POST",
        {}, 
        {
            Authorization: `${token}`,
        })

    } catch (error) {
        return Promise.reject(error);
    }
};
