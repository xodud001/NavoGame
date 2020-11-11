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

    public void drawCrewmates(SpriteBatch batch, Crewmate user){
        for(Crewmate crewmate : crewmates){
            if(!user.owner.equals(crewmate.owner))
                crewmate.draw(batch);
        }
    }

    public int getRoomCode(){
        return roomCode;
    }
    public void roomUpdate(JSONObject roomInfo, World world, TextureAtlas atlas){
        if(this.roomCode == Integer.parseInt(roomInfo.get("code").toString()) ){
            JSONObject crewmatesJson = (JSONObject)roomInfo.get("crewmates");
            int i=0;
            while(crewmatesJson.get("" + i) != null){
                for(Crewmate crewmate : crewmates){
                    JSONObject temp = (JSONObject)crewmatesJson.get("" + i);
                    if(temp != null && crewmate.owner.equals(temp.get("owner"))){
                        crewmate.updateInfo(temp);
                        i++;
                        break;
                    }
                }
                //addCrewmate( (JSONObject)crewmatesJson.get("" + i) , world, atlas);
                i++;
            }
        }
    }

    public void addCrewmate(JSONObject crewmateJson, World world, TextureAtlas atlas){
        crewmates.add(new Crewmate(world, atlas, crewmateJson));
    }
}
