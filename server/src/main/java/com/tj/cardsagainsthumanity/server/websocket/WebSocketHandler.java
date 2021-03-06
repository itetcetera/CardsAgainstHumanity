package com.tj.cardsagainsthumanity.server.websocket;

import com.tj.cardsagainsthumanity.security.auth.PlayerUserDetails;
import com.tj.cardsagainsthumanity.server.protocol.CommandProcessor;
import com.tj.cardsagainsthumanity.server.protocol.io.impl.JSONSerializer;
import com.tj.cardsagainsthumanity.server.protocol.io.impl.WebSocketProtocolWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final JSONSerializer serializer;
    private final CommandProcessor commandProcessor;
    private WebSocketConnectionManager connectionManager;

    public WebSocketHandler(@Qualifier("genericProcessor") @Autowired CommandProcessor commandProcessor, @Autowired WebSocketConnectionManager connectionManager, @Autowired JSONSerializer serializer) {
        this.connectionManager = connectionManager;
        this.serializer = serializer;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        establishConnection(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        connectionManager.removeConnection(session.getId());
    }

    private WebSocketConnection createWebSocketConnection(WebSocketSession session) {
        WebSocketProtocolWriter writer = new WebSocketProtocolWriter(session, serializer);
        Authentication auth = (Authentication) session.getPrincipal();
        PlayerUserDetails user = (PlayerUserDetails) auth.getPrincipal();
        WebSocketConnection connection = new WebSocketConnection(session, writer, commandProcessor);
        connection.getConnectionContext()
                .login(user.getPlayer());
        return connection;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        WebSocketConnection connection = connectionManager.getConnection(session.getId());
        if (connection == null) {
            connection = establishConnection(session);
            connection = createWebSocketConnection(session);
            connectionManager.addConnection(session.getId(), connection);
        }
        connection.onDataReceived(message.getPayload());

    }

    private WebSocketConnection establishConnection(WebSocketSession session) {
        WebSocketConnection connection = createWebSocketConnection(session);
        connectionManager.addConnection(session.getId(), connection);
        return connection;
    }
}
