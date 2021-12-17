package cz.cvut.felk.kbss.freeplane.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    @Value("${local.server.port}")
    private int port;

    private String url;
    private WebSocketStompClient stompClient;

    private CompletableFuture<String> completableFuture;

    @Before
    public void setUp() {
        completableFuture = new CompletableFuture<>();
        url = "ws://localhost:" + port + "/mindmap";
        stompClient =  new WebSocketStompClient(new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    public void test() throws ExecutionException, InterruptedException, TimeoutException {

        stompClient.setMessageConverter(new StringMessageConverter());

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setLogin("mail@mail.cz");

        StompSession stompSession = stompClient.connect(url, (WebSocketHttpHeaders) null, stompHeaders, new StompSessionHandlerAdapter() {
        }).get();

        stompSession.subscribe("topic/mindmaps/2", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                completableFuture.complete((String) o);
            }
        });

        stompSession.send("app/mindmaps/2", "some text");

       // stompClient.stop();

        String response = completableFuture.get(10, SECONDS);

        assertNotNull(response);
    }
}
