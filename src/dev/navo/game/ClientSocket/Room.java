package dev.navo.game.ClientSocket;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import dev.navo.game.Sprites.Crewmate;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Room { // 게임 방
    int roomCode;
    ArrayList<Crewmate> crewmates;

    public Room(World world, TextureAtlas atlas, JSONObject roomInfo){
        crewmates = new ArrayList<>();

        this.roomCode = Integer.parseInt(roomInfo.get("code").toString()) ;
        JSONObject crewmatesJson = (JSONObject)roomInfo.get("crewmates");

        int i = 0;
        while(crewmatesJson.get("" + i) != null){
            crewmates.add(new Crewmate(world, atlas, (JSONObject)crewmatesJson.get("" + i++)));
        }
    }

    public void drawCrewmates(SpriteBatch batch){
        for(Crewmate crewmate : crewmates){
            crewmate.draw(batch);
        }
    }
}
