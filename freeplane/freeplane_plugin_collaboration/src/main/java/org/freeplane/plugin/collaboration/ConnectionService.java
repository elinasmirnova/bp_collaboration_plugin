package org.freeplane.plugin.collaboration;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.features.map.*;
import org.freeplane.features.map.mindmapmode.MMapModel;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.mode.ModeController;
import org.freeplane.n3.nanoxml.XMLException;
import org.freeplane.plugin.collaboration.model.ServerMindMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Provides connection services to send requests to the server.
 * Caches all the needed information about a current user and collaboration stuff.
 */
public class ConnectionService {

    private static StompSession currentStompSession;
    private static String email;
    private static ServerMindMap mindMap;
    private static String authorizationHeader;
    private static boolean logged;
    private static ConnectionService connectionService = null;

    private final HttpClient httpClient;

    private ConnectionService() {
        this.httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    }

    /**
     * Sends a request to authenticate a user.
     *
     * @param email    user's e-mail from a text field
     * @param password user's password from a text field
     * @return boolean, if the authentication was successes
     * @throws IOException
     */
    public boolean userAuthentication(String email, String password) throws IOException {
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Please fill in your e-mail and password", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        HttpPost authenticationRequest = new HttpPost("http://localhost:8080/auth/login");
        String inputJson = "{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";
        StringEntity requestEntity = new StringEntity(
                inputJson,
                ContentType.APPLICATION_JSON);
        authenticationRequest.setEntity(requestEntity);

        HttpResponse response = this.httpClient.execute(authenticationRequest);

        if (response.getStatusLine().getStatusCode() == 200) {
            String responseBody = EntityUtils.toString(response.getEntity());
            final JSONObject object = new JSONObject(responseBody);
            authorizationHeader = object.getString("token");
            ConnectionService.email = email;
            logged = true;
            return true;
        } else {
            logged = false;
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "E-mail or password is wrong, please try again", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Sends a request to fetch user's mind maps from the server.
     *
     * @return collection of Strings, which consists of (mind map id) + title
     * @throws IOException
     */
    public List<String> getMindmaps() throws IOException {
        HttpGet getMindmapsRequest = new HttpGet("http://localhost:8080/mindmaps");
        getMindmapsRequest.addHeader("Authorization", authorizationHeader);

        final HttpResponse response = httpClient.execute(getMindmapsRequest);
        final String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() == 200) {
            return parseMindmaps(responseEntity);
        } else {
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while fetching mind maps from server, please try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Parses JSON object from the response to obtain user's mind maps.
     *
     * @param responseBody body from the response
     * @return collection of the parsed user's mind maps
     */
    private List<String> parseMindmaps(String responseBody) {
        List<String> parsedMindmaps = new ArrayList<>();

        final JSONObject object = new JSONObject(responseBody);
        final JSONArray jsonYourMindmaps = object.getJSONArray("yourMindmaps");
        final JSONArray jsonSharedMindmaps = object.getJSONArray("sharedMindmaps");

        for (int i = 0; i < jsonYourMindmaps.length(); i++) {
            final JSONObject mindmap = jsonYourMindmaps.getJSONObject(i);
            parsedMindmaps.add("(" + mindmap.get("mindmapId").toString() + ") " + mindmap.getString("title"));
        }
        for (int i = 0; i < jsonSharedMindmaps.length(); i++) {
            final JSONObject mindmap = jsonSharedMindmaps.getJSONObject(i);
            parsedMindmaps.add("(" + mindmap.get("mindmapId").toString() + ") " + mindmap.getString("title"));
        }
        return parsedMindmaps;
    }

    /**
     * Sends the request to fetch mind map from the server by its id.
     *
     * @param mindMapId mind map Id
     * @return mind map DTO from the server
     * @throws IOException
     */
    public ServerMindMap getMindmap(String mindMapId) throws IOException {
        HttpGet getMindmapsRequest = new HttpGet("http://localhost:8080/mindmaps/" + mindMapId);
        getMindmapsRequest.addHeader("Authorization", authorizationHeader);

        HttpResponse response = httpClient.execute(getMindmapsRequest);
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 200) {
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while fetching mind map, please try again", "Error", JOptionPane.ERROR_MESSAGE);
        }

        final JSONObject object = new JSONObject(responseEntity);
        final String mindmap = object.getString("xml");
        final boolean isReadOnly = object.getBoolean("readOnly");

        return new ServerMindMap(mindMapId, Base64.decodeBase64(mindmap), isReadOnly);
    }

    /**
     * Defines WebSocket STOMP client to connect and establish connection to the WebSocket server.
     *
     * @param serverMindMap mind map from the server
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void openMindMapWebsockets(ServerMindMap serverMindMap) throws ExecutionException, InterruptedException {
        mindMap = serverMindMap;

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        WebSocketClient webSocketClient = new SockJsClient(transports);

        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(
                webSocketClient);
        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("email", email);
        stompHeaders.set("mindmap", mindMap.getMindMapId());
        stompHeaders.set("Authorization", authorizationHeader);

        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization", authorizationHeader);

        ListenableFuture<StompSession> session = webSocketStompClient.connect(
                "http://localhost:8080/websocket",
                webSocketHttpHeaders,
                stompHeaders,
                stompSessionHandler());
        currentStompSession = session.get();
    }

    private StompSessionHandler stompSessionHandler() {
        return new ClientStompSessionHandler();
    }

    /**
     * Sends the PUT request to update mind map's content.
     *
     * @param mindmap   mind map's content in bytes
     * @param mindmapId mind map's ID
     * @throws IOException
     */
    public void updateMindmap(byte[] mindmap, String mindmapId) throws IOException {
        HttpPut request = new HttpPut("http://localhost:8080/mindmaps/" + mindmapId + "/content");

        String inputJson = new String(mindmap, StandardCharsets.UTF_8);
        StringEntity requestEntity = new StringEntity(
                inputJson,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);
        request.addHeader("Authorization", authorizationHeader);

        HttpResponse response = this.httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while saving mind map, please try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sends the POST request to save a new mind map on the server.
     *
     * @param mindMap  mind map's content in bytes
     * @param title    mind map's title
     * @param isPublic boolean, if the mind map is public or not
     * @throws IOException
     */
    public void saveMindmap(byte[] mindMap, String title, boolean isPublic) throws IOException {
        HttpPost request = new HttpPost("http://localhost:8080/mindmaps");

        String stringMindMap = Base64.encodeBase64String(mindMap);

        String inputJson = "{\n" +
                "  \"map\": \"" + stringMindMap + "\",\n" +
                "  \"title\": \"" + title + "\",\n" +
                "  \"isPublic\": \"" + isPublic + "\"\n" +
                "}";
        StringEntity requestEntity = new StringEntity(
                inputJson,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        HttpResponse response = this.httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == 201) {
            Controller.getCurrentController().getMapViewManager().closeWithoutSaving();
        } else if (response.getStatusLine().getStatusCode() == 409) {
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Mind map with title already exists, please choose another title", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatusLine().getStatusCode() != 201) {
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while saving mind map, please try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens a mind map from the bytes.
     *
     * @param in input mind map in bytes
     * @throws IOException
     * @throws XMLException
     */
    public void openMapFromServer(final InputStream in, boolean isReadOnly) throws IOException, XMLException {
        final ModeController modeController = Controller.getCurrentModeController();
        final MapController mapController = modeController.getMapController();
        final MapModel map = new MMapModel(mapController.duplicator());
        map.setReadOnly(isReadOnly);
        InputStreamReader urlStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        modeController.getMapController().getMapReader().createNodeTreeFromXml(map, urlStreamReader, MapWriter.Mode.FILE);
        map.setSaved(false);
        mapController.fireMapCreated(map);
        mapController.createMapView(map);
    }

    // GETTERS
    public static ConnectionService getInstance() {
        if (connectionService == null) {
            connectionService = new ConnectionService();
        }
        return connectionService;
    }

    public static boolean isLogged() {
        return logged;
    }

    public static StompSession getCurrentStompSession() {
        return currentStompSession;
    }

    public static String getEmail() {
        return email;
    }

    public static ServerMindMap getMindMap() {
        return mindMap;
    }

    public static String getAuthorizationHeader() {
        return authorizationHeader;
    }

    // SETTERS
    public static void setCurrentStompSession(StompSession currentStompSession) {
        ConnectionService.currentStompSession = currentStompSession;
    }

    public static void setEmail(String email) {
        ConnectionService.email = email;
    }

    public static void setMindMap(ServerMindMap mindMap) {
        ConnectionService.mindMap = mindMap;
    }

    public static void setAuthorizationHeader(String authorizationHeader) {
        ConnectionService.authorizationHeader = authorizationHeader;
    }
}
