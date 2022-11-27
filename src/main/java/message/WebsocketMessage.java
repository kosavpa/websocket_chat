package message;


import lombok.Data;

import java.util.Set;


@Data
public class WebsocketMessage {
    private MessageType type;
    private String from;
    private Set<String> userList;
    private String content;
    private boolean resultConnectStatus;

    public WebsocketMessage(MessageType type, boolean resultConnectStatus, String content) {
        this.type = type;
        this.content = content;
        this.resultConnectStatus = resultConnectStatus;
    }

    public WebsocketMessage(MessageType type, String from, String content) {
        this.type = type;
        this.from = from;
        this.content = content;
    }

    public WebsocketMessage(MessageType type, Set<String> userList) {
        this.type = type;
        this.userList = userList;
    }
}
