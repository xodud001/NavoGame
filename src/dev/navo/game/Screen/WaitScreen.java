package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Room;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Bullet;
import dev.navo.game.Sprites.Crewmate;
import dev.navo.game.Tools.B2WorldCreator;
import dev.navo.game.Tools.Util;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class WaitScreen implements Screen {    private NavoGame game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private Texture background;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate myCrewmate;
    private ArrayList<Crewmate> crewmates;

    private ArrayList<Rectangle> blocks;

    private Vector2 centerHP;

    private Room room;

    ShapeRenderer shapeRenderer;

    private String mapType = "Wait.tmx";
    private static final int moveSpeed = 10;
    private static int maxSpeed = 80;

    public WaitScreen(NavoGame game, JSONObject roomInfo){

        atlas = new TextureAtlas("Image.atlas");
        centerHP = new Vector2(375, 325);
        shapeRenderer = new ShapeRenderer();
        this.game = game;
        background = new Texture("data/GameBack.png");
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapType);
        renderer = new OrthogonalTiledMapRenderer(map);
        gameCam.position.set(100,100, 0); // 200, 1130 = Left Top

        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        B2WorldCreator b2 = new B2WorldCreator(world, map);
        blocks = new ArrayList<>();
        blocks = b2.getRecList();

        System.out.println(roomInfo.toJSONString());
        room = new Room(world, atlas, roomInfo);

        myCrewmate = new Crewmate(world, atlas, new Vector2(100, 100), "상민이", "Purple");

        crewmates = new ArrayList<>();
        crewmates.add(myCrewmate);
        hud.addLabel(myCrewmate.getLabel());

    }

    public void update(float dt){
        Util.moveInputHandle(dt, myCrewmate, maxSpeed, moveSpeed);

        Util.frameSet(world);

        //c1.update(dt);
        for(int i = 0; i < crewmates.size() ; i++){
            Crewmate temp = crewmates.get(i);
            if(temp.getHP() == 0){
                world.destroyBody(temp.b2Body);
                hud.removeActor(temp.getLabel());
                crewmates.remove(i--);
                continue;
            }
            temp.update(dt);
        }

        hud.showMessage("c1.velocity"+ myCrewmate.b2Body.getLinearVelocity().toString());

        gameCam.position.x = myCrewmate.b2Body.getPosition().x;
        gameCam.position.y = myCrewmate.b2Body.getPosition().y;

        gameCam.update();
        renderer.setView(gameCam);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background,0 ,0 );
        game.batch.end();

        renderer.render();
        b2dr.render(world, gameCam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        myCrewmate.draw(game.batch);
        shapeRenderer.rect(centerHP.x ,centerHP.y, 50 * (myCrewmate.getHP() / myCrewmate.getMaxHP()), 10);

        for(Crewmate c : crewmates) {
            c.draw(game.batch);
            if(!c.equals(myCrewmate)) {
                shapeRenderer.rect(centerHP.x + (c.b2Body.getPosition().x - myCrewmate.b2Body.getPosition().x) * 2,
                        centerHP.y + (c.b2Body.getPosition().y - myCrewmate.b2Body.getPosition().y) * 2, 50 * (c.getHP() / c.getMaxHP()), 10);

                c.getLabel().setPosition(174 + (c.b2Body.getPosition().x - myCrewmate.b2Body.getPosition().x),
                        165 + (c.b2Body.getPosition().y - myCrewmate.b2Body.getPosition().y));

            }else{
                myCrewmate.getLabel().setPosition(174, 166);
            }
        }
        room.drawCrewmates(game.batch);

        game.batch.end();

        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
