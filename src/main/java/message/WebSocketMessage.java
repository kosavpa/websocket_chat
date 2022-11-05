package message;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class WebSocketMessage {
    private String from;
    private String chat;
    private String type;
    private String content;
}
