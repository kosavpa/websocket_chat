package endpoint;


import message.MessageType;
import message.WebsocketMessage;
import utile.WebSocketMessageDecoder;
import utile.WebSocketMessageEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@ServerEndpoint(value = "/websocket_chat/{username}",
        decoders = WebSocketMessageDecoder.class,
        encoders = WebSocketMessageEncoder.class)
class ChatEndpoint {
    private Map<Session, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username")String username) throws IOException {
        if(!users.containsValue(username)) {
            users.put(session, username);

            session.getAsyncRemote().sendObject(new WebsocketMessage(MessageType.CONNECTION, true, "Congratulate, good connection!"));

            generalSending(new WebsocketMessage(MessageType.USER_LIST, (Set<String>) users.values()));
        } else {
            session.getAsyncRemote().sendObject(new WebsocketMessage(MessageType.CONNECTION, false, "This username is already exist!"));
        }
    }

    @OnMessage
    public void onMessage(Session session, WebsocketMessage message) throws IOException {
        generalSending(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        users.remove(session);

        generalSending(new WebsocketMessage(MessageType.USER_LIST, (Set<String>) users.values()));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private void generalSending(WebsocketMessage message) {
        synchronized (users) {
            for (Session userSession : users.keySet()) {
                userSession.getAsyncRemote().sendObject(message);
            }
        }
    }
}