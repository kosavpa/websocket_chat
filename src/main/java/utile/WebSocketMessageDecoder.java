package utile;


import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import message.WebsocketMessage;


public class WebSocketMessageDecoder implements Decoder.Text<WebsocketMessage> {
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
    public WebsocketMessage decode(String message) throws DecodeException {
        return jsonDecoder.fromJson(message, WebsocketMessage.class);
    }

    @Override
    public boolean willDecode(String message) {
        return message != null;
    }
    
}
