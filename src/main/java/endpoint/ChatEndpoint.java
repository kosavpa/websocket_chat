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
public class ChatEndpoint {
    private static Map<Session, String> allConnection = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        if(!allConnection.containsValue(username)) {
            allConnection.put(session, username);
            session.getAsyncRemote().sendObject(new WebsocketMessage(MessageType.CONNECTION, true, "Congratulate, good connection!"));
            generalSending(new WebsocketMessage(MessageType.USER_LIST, (Set<String>) allConnection.values()));
        } else {
            session.getAsyncRemote().sendObject(new WebsocketMessage(MessageType.CONNECTION, false, "Bad connection, people with this is username exist!"));
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, WebsocketMessage message) throws IOException {
        generalSending(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        allConnection.remove(session);
        generalSending(new WebsocketMessage(MessageType.USER_LIST, (Set<String>) allConnection.values()));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private void generalSending(WebsocketMessage message) {
            for (Session userSession : allConnection.keySet())
                if(allConnection.get(userSession) != null)
                    userSession.getAsyncRemote().sendObject(message);
    }
}