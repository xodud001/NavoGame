package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import dev.navo.game.Tools.FontGenerator;
import org.json.simple.JSONObject;

public class Crewmate  extends Sprite {

    public World world;
    public Body b2Body;

    //Image and animation
    private TextureRegion crewmateFrontStand;
    private TextureRegion crewmateBackStand;
    private TextureRegion crewmateRightStand;
    private TextureRegion crewmateLeftStand;
    private Animation crewmateFront;
    private Animation crewmateBack;
    private Animation crewmateLeft;
    private Animation crewmateRight;

    //캐릭터 정보
    public String owner;
    private String color;
    private String name;
    private Label nameLabel;
    private float maxHP;
    private float HP;
    private float attackDelay;
    private boolean isStop;
    private float stateTimer;

    public enum State { UP, DOWN, LEFT, RIGHT };
    public State currentState;
    public State previousState;

    private final static float frameDuration = (float) 0.2;
    public float getStateTimer(){
        return stateTimer;
    }
    public float getAttackDelay(){
        return attackDelay;
    }

    public float getMaxHP() { return maxHP;}
    public float getHP() { return HP;}
    public void hit() {
        if(HP != 0) this.HP--;
    }
    public Label getLabel(){
        return nameLabel;
    }

    public void setAttackDelay(float delay){
         this.attackDelay = delay;
    }

    public Crewmate(World world, TextureAtlas atlas, JSONObject crewmateJson) {
        super(atlas.findRegion(crewmateJson.get("color").toString()));
        this.world = world;
        this.maxHP = Integer.parseInt(crewmateJson.get("maxHP").toString());
        this.HP = Integer.parseInt(crewmateJson.get("HP").toString());
        this.color = crewmateJson.get("color").toString();
        this.name = crewmateJson.get("name").toString();

        nameLabel = new Label(name, new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        nameLabel.setWidth(50);
        nameLabel.setHeight(15);
        nameLabel.setFontScale(0.25f);
        nameLabel.setAlignment(Align.center);

        owner = crewmateJson.get("owner").toString();
        isStop = crewmateJson.get("isStop").toString().equals("true");

        currentState = previousState = State.valueOf(crewmateJson.get("state").toString()) ;
        stateTimer = Float.parseFloat(crewmateJson.get("stateTimer").toString());
        initFrame();

        setBounds( Integer.parseInt(crewmateJson.get("x").toString()) - 11
                , Integer.parseInt(crewmateJson.get("y").toString())-12
                , 20
                , 25);
        setRegion(crewmateFrontStand);
    }

    public Crewmate(World world, TextureAtlas atlas, Vector2 v, String name, String color){
        super(atlas.findRegion(color));
        this.world = world;
        this.maxHP = 10;
        this.HP = 10;
        this.color = color;
        this.name = name;
        nameLabel = new Label(name, new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        nameLabel.setWidth(50);
        nameLabel.setHeight(15);
        nameLabel.setFontScale(0.25f);
        nameLabel.setAlignment(Align.center);


        currentState = State.DOWN;
        previousState = State.DOWN;
        stateTimer = 0;
        attackDelay = 0f;

        initFrame();

        defineCrewmate(v);
        setBounds(200-11, 500-12, 20, 25);
        setRegion(crewmateFrontStand);
    }

    private void initFrame(){
        int regionX = getImageStartX(color);
        Array<TextureRegion> frames = new Array<>();
        //Down animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX,  50+12, 20, 25));
        crewmateFront = new Animation(frameDuration, frames);
        frames.clear();

        //Up animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX, 75+12, 20, 25));
        crewmateBack = new Animation(frameDuration, frames);
        frames.clear();

        //Left animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX, 25+12, 20, 25));
        crewmateLeft = new Animation(frameDuration, frames);
        frames.clear();

        //Right animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX, 12, 20, 25));
        crewmateRight = new Animation(frameDuration, frames);
        frames.clear();

        crewmateFrontStand = new TextureRegion(getTexture(), regionX, 50+12, 20, 25);
        crewmateBackStand = new TextureRegion(getTexture(), regionX, 75+12, 20, 25);
        crewmateLeftStand = new TextureRegion(getTexture(), regionX, 25+12, 20, 25);
        crewmateRightStand = new TextureRegion(getTexture(), regionX, 12, 20, 25);
    }

    private int getImageStartX(String color) {
        switch (color) {
            case "Blue":
                return 1;
            case "Gray":
                return 91;
            case "Green":
                return 181;
            case "Purple":
                return 271;
            default: // Red
                return 361;
        }
    }

    public void defineCrewmate(Vector2 v){
        BodyDef bDef = new BodyDef();
        bDef.position.set(v.x,v.y);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8.5f, 11);

        fDef.shape = shape;
        b2Body.createFixture(fDef);
    }

    public void update(float dt){
        if(attackDelay > 0) attackDelay -= dt;

        setPosition(b2Body.getPosition().x - getWidth() /2 -1, b2Body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        if(isStop){
            previousState = currentState;
            stateTimer = 0;
            switch(previousState){
                case UP:
                    region = crewmateBackStand;
                    break;
                case LEFT:
                    region = crewmateLeftStand;
                    break;
                case RIGHT:
                    region = crewmateRightStand;
                    break;
                default:
                    region = crewmateFrontStand;
                    break;
            }
            return region;
        }
        switch (currentState){
            case UP:
                region = (TextureRegion)crewmateBack.getKeyFrame(stateTimer);
                break;
            case DOWN:
                region = (TextureRegion)crewmateFront.getKeyFrame(stateTimer);
                break;
            case RIGHT:
                region = (TextureRegion)crewmateRight.getKeyFrame(stateTimer);
                break;
            case LEFT:
                region = (TextureRegion)crewmateLeft.getKeyFrame(stateTimer);
                break;
            default:
                region = crewmateFrontStand;
                break;
        }
        if(stateTimer >= frameDuration*3)
            stateTimer = 0;

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        isStop = false;
        if(b2Body.getLinearVelocity().x > 0)
            return State.RIGHT;
        else if(b2Body.getLinearVelocity().x < 0)
            return State.LEFT;
        else if(b2Body.getLinearVelocity().y > 0)
            return State.UP;
        else if(b2Body.getLinearVelocity().y < 0)
            return State.DOWN;
        else {
            isStop = true;
            return currentState;
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getCrewmateJson(){
        JSONObject result = new JSONObject();

        result.put("owner", owner);
        result.put("x", getX());
        result.put("y", getY());
        result.put("name", name);
        result.put("color", color);
        result.put("state", currentState.toString());
        result.put("maxHP", maxHP);
        result.put("HP", HP);
        result.put("isStop", isStop);
        result.put("stateTimer", stateTimer);

        return result;
    }
}
