package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Room { // 게임 방

    private static Room room;

    private static Crewmate2D myCrewmate;

    int roomCode;
    ArrayList<CrewmateMulti> crewmates;

    public Room(){
        this.crewmates = new ArrayList<>();
    }
    public Crewmate2D getMyCrewmate(){
        return myCrewmate;
    }
    public void roomInit(JSONObject roomInfo) throws ParseException {
        this.roomCode = Integer.parseInt(roomInfo.get("code").toString()) ;
        JSONObject crewmatesJson = (JSONObject)roomInfo.get("crewmates");
        int i = 0;
        while(crewmatesJson.get("" + i) != null){
            CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, (JSONObject)crewmatesJson.get("" + i));
            crewmates.add(temp);
            i++;
        }
    }

    public static Room getRoom(){
        if(room == null){
            room = new Room();
        }
        return room;
    } // 싱글톤 게터

    public static void setMyCrewmate(Crewmate2D crewmate){
        myCrewmate = crewmate;
        Client.getInstance().enter(crewmate.getCrewmateInitJson());
    }




    public void drawCrewmates(SpriteBatch batch, String user){
        for(CrewmateMulti crewmate : crewmates){
            if(!user.equals(crewmate.owner)) {
                crewmate.draw(batch);
            }
        }
    }

    public ArrayList<CrewmateMulti> getCrewmates(){
        return crewmates;
    }
    public int getRoomCode(){
        return roomCode;
    }

    public void roomUpdate(JSONObject roomInfo){
        if(this.roomCode == Integer.parseInt(roomInfo.get("code").toString()) ){
            JSONObject crewmatesJson = (JSONObject)roomInfo.get("crewmates");
            int size = Integer.parseInt(crewmatesJson.get("crewmates_size").toString());
            for(int i = 0 ; i < size ; i++){
                boolean isFine = false;
                JSONObject temp = (JSONObject)crewmatesJson.get("" + i);
                String owner = temp.get("owner").toString();
                for(CrewmateMulti crewmate : crewmates){
                    if(crewmate.owner.equals(owner)){
                        crewmate.updateInfo(temp);
                        isFine = true;
                    }
                }
                if (!isFine) addCrewmate(temp);
            }
        }
    }

    public void addCrewmate(JSONObject crewmateJson){
        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, crewmateJson);
        crewmates.add(temp);
    }

    public void roomNewUserEnter(JSONObject body) {
        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, body);
        crewmates.add(temp);
    }
}
