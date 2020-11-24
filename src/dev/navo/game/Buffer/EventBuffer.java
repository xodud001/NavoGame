package dev.navo.game.Buffer;

import org.json.simple.JSONObject;

public class EventBuffer extends Buffer{
    private static EventBuffer instance=null;
    public static EventBuffer getInstance() {
        if(instance == null) instance = new EventBuffer();
        return instance;
    }

}
