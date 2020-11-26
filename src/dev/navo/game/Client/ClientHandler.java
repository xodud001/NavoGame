package dev.navo.game.Client;

import dev.navo.game.Buffer.EventBuffer;
import dev.navo.game.Buffer.InGameBuffer;
import dev.navo.game.Buffer.LoginBuffer;
import dev.navo.game.Tools.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ClientHandler  extends ChannelInboundHandlerAdapter {

    EventBuffer eventBuffer = EventBuffer.getInstance();
    InGameBuffer inGameBuffer = InGameBuffer.getInstance();
    LoginBuffer loginBuffer = LoginBuffer.getInstance();
    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        String msg = arg1.toString();
        JSONObject json = JsonParser.createJson(msg);

        String header = json.get("Header").toString();

        if(header.equals("Auth")) {
            loginBuffer.put( JsonParser.createJson(json.get("Body").toString()) );
        }else if(header.equals("Event")) {
            eventHandler(JsonParser.createJson(json.get("Body").toString()));
        }else if(header.equals("InGame")) {
            inGameBuffer.put(JsonParser.createJson(json.get("Body").toString()));
        }
    }

    private void eventHandler(JSONObject body) throws ParseException {
        String function = body.get("Function").toString();

        if(function.equals("5")){
            Room.getRoom().roomInit(body);
        }else if(function.equals("10")){
            Room.getRoom().roomNewUserEnter((JSONObject)body.get("crewmate"));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
