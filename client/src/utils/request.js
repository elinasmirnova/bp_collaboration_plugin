export const request = async (
    url,
    method = "GET",
    body = null,
    headers = {},
    isFile = false
) => {
    try {
        if (!isFile) {
            if (body) {
                body = JSON.stringify(body);
            }
        }
        const response = await fetch(url, {
        method, body, headers});
        if (!response.ok) {
            return Promise.reject({
                statusCode: response.status,
                statusText: response.statusText,
            });
        }
        
        if (method === 'GET'){
            const data = await response.json();
            return data;
        }
        else{
            const data = await response;
            return data;
        }
        
    } catch (error) {
        console.log(`Request failed. Reason: ${error}`);
        return Promise.reject(error);
    }
};


