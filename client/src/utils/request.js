export const request = async (
    url,
    options
) => {
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            return Promise.reject({
                status: response.status,
                statusText: response.statusText,
            });
        }

        if (options.method === 'GET') {
            const data = await response.json();
            return data;
        }
        else {
            const data = await response;
            return data;
        }
    } catch (error) {
        return Promise.reject(error);
    }
};


