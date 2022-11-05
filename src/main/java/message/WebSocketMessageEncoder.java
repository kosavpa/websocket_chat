package message;


import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;


public class WebSocketMessageEncoder implements Encoder.Text<WebSocketMessage> {
    private static Gson jsonEncoder = new Gson();

    @Override
    public void destroy() {
        // nop
    }

    @Override
    public void init(EndpointConfig nop) {
        // nop
    }

    @Override
    public String encode(WebSocketMessage message) throws EncodeException {       
        return jsonEncoder.toJson(message);
    }   
}