package endpoint;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import message.WebSocketMessage;
import message.WebSocketMessageDecoder;
import message.WebSocketMessageEncoder;
import user.WebSocketUser;

@ServerEndpoint(value = "/websocket/{username}",
                decoders = WebSocketMessageDecoder.class,
                encoders = WebSocketMessageEncoder.class)
public class WebSocketEndPoint {
    private Set<WebSocketUser> chatList = new HashSet<WebSocketUser>();
    private String MSG_CONNECTION = "{'type':'connection', 'username':'%s', 'result':%s}";
    private String MSG_USER_LIST  = "{'type':'userlist','users':[%s]}";
   
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        String   chat = null;
        String   user = null;

        String[] data = username.split("_");

        if (data.length > 1) {
            chat  = data[0];
            user  = data[1];
        } else {
            user  = username;
        }
        
        String  result = String.valueOf(addUser(session, chat, user));

        synchronized (session) {
            session.getAsyncRemote().sendText(String.format(MSG_CONNECTION, chat, user, result));
        }

        if (chatList.size() > 0) {
            userListDistribution(chat);
        }
    }

    @OnMessage
    public void onMessage(Session session, WebSocketMessage message) throws IOException, EncodeException {
        if (message.getType().equalsIgnoreCase("message"))
            broadcast(message);
    }
    
    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        WebSocketUser wsUser = getWebSocketUser(session);
        chatList.remove(wsUser);
        userListDistribution(wsUser.getChat());
    }
    
    @OnError
    public void onError(Session session, Throwable t) {
    }
    
    private void userListDistribution(String chat) throws IOException, EncodeException {
        StringBuilder users = new StringBuilder();

        for(WebSocketUser wsUser : chatList){
            users.append(String.format("\"%s\", ", wsUser.getUser()));
        }
        
        String userList = users.toString();

        for(WebSocketUser wsUser : chatList){
            if (wsUser.getChat().equalsIgnoreCase(chat) && wsUser.getSession().isOpen()){
                synchronized (wsUser.getSession()) {
                    wsUser.getSession().getAsyncRemote().sendText(String.format(MSG_USER_LIST, userList));
                }
            }
        }            
    }
    
    private boolean addUser(final Session session, final String chatName, final String username) {
        boolean result = false;

        for (WebSocketUser user : chatList) {
            if (user.getChat().equalsIgnoreCase(chatName) && user.getUser().equalsIgnoreCase(username)) {
                return result;
            }
        }
        
        result = chatList.add(new WebSocketUser(session, chatName, username));

        return result;
    }
    
    private WebSocketUser getWebSocketUser(Session session) {
        WebSocketUser user = null;
        for (WebSocketUser wsUser : chatList) {
            if (wsUser.getSession().getId().equals(session.getId())){
                user = wsUser;
            }
        }

        return user;
    }
    
    private void broadcast(WebSocketMessage message) throws IOException, EncodeException {
        for(WebSocketUser wsUser : chatList){
            if (wsUser.getChat().equalsIgnoreCase(message.getChat())) {
                synchronized (wsUser) {
                        wsUser.getSession().getAsyncRemote().sendObject(message);
                    }
                }
            }
        }
    }
