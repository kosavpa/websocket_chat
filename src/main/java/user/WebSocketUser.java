package user;


import javax.websocket.Session;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class WebSocketUser {
    private Session session;
    private String chat;
    private String user;
}
