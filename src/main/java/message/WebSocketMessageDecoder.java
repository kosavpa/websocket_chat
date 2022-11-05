package message;


import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;


public class WebSocketMessageDecoder implements Decoder.Text<WebSocketMessage> {
    private static Gson jsonDecoder = new Gson();

    @Override
    public void destroy() {
        //nop
        
    }

    @Override
    public void init(EndpointConfig arg0) {
        //nop
        
    }

    @Override
    public WebSocketMessage decode(String message) throws DecodeException {
        return jsonDecoder.fromJson(message, WebSocketMessage.class);
    }

    @Override
    public boolean willDecode(String message) {
        return message != null;
    }
    
}
