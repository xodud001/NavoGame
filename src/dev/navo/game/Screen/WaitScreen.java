package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.Buffer.EventBuffer;
import dev.navo.game.Client.Client;
import dev.navo.game.Client.Room;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Tools.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class WaitScreen implements Screen {    private NavoGame game;

    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate2D myCrewmate;

    private Vector2 centerHP;

    private Room room;

    private Label players;

    private TextButton startBtn;
    private TextButton backBtn;

    private ShapeRenderer shapeRenderer;

    Client client;
    private String mapType = "Wait.tmx";
    private static final int moveSpeed = 10;
    private static int maxSpeed = 80;

    public WaitScreen(NavoGame game) throws ParseException {
        client = Client.getInstance();
        atlas = new TextureAtlas("Image.atlas");
        centerHP = new Vector2(375, 325);
        shapeRenderer = new ShapeRenderer();
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapType);
        renderer = new OrthogonalTiledMapRenderer(map);
        gameCam.position.set(100,100, 0); // 200, 1130 = Left Top

        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);

        myCrewmate = new Crewmate2D(world, atlas, new Vector2(100, 100), "상민이", "Purple", client.getOwner());
        client.enter(myCrewmate.getCrewmateInitJson());
        JSONObject roomInfo = EventBuffer.getInstance().get();
        room = new Room(world, atlas, roomInfo, hud);

        initComponent();
        btnsAddListener();

        client.setIsInGameThread(true);
        client.updateSender(myCrewmate, room);
        client.updateReceiver(room, world, atlas, hud);
        client.eventHandler(room, hud);
    }
    //컴포넌트 초기화
    private void initComponent(){
        players = new Label( ""+ room.getCrewmates().size(), new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE ));
        players.setBounds(370, 270, 30, 30);

        startBtn = new TextButton( "START", Util.skin );
        startBtn.setBounds(170, 5, 60, 24);

        backBtn = new TextButton( "EXIT", Util.skin );
        backBtn.setBounds(350, 5, 40, 24);

        hud.addActor(startBtn);
        hud.addActor(backBtn);
        hud.addActor(players);
        hud.addActor(myCrewmate.getLabel());

        Gdx.input.setInputProcessor(hud.stage);
    }
    //버튼 리스너
    private void btnsAddListener(){
        startBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                startBtn.clear();
                backBtn.clear();
                Sounds.start.play(); // 게임 시작 사운드 출력
                game.setScreen(new PlayScreen(game)); // PlayScreen으로 넘어가기
                dispose();
            }
        });

        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                startBtn.clear();
                backBtn.clear();
                Sounds.click.play();
                game.setScreen(new LobbyScreen(game));
                client.exit(room.getRoomCode());
                dispose();
            }
        });
    }

    // 키 입력 처리 메소드
    public void handleInput(float dt){
        Util.moveInputHandle(myCrewmate, maxSpeed, moveSpeed); // 내 캐릭터 움직임 처리
    }


    public void update(float dt) throws IOException, ParseException {
        handleInput(dt);
        Util.frameSet(world); // FSP 60으로 설정

        myCrewmate.update(dt); // 내 캐릭터 업데이트
        for(CrewmateMulti c :  room.getCrewmates()) c.update(dt); // 방에 있는 캐릭터들 업데이트

        hud.showMessage("Room Code : "+ room.getRoomCode());
        players.setText(room.getCrewmates().size());
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
        try {
            update(delta);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        Images.renderBackground(delta, game.batch);
        game.batch.end();

        renderer.render();
        b2dr.render(world, gameCam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();

        room.drawCrewmates(game.batch, myCrewmate.owner);

        for(CrewmateMulti c : room.getCrewmates()) {
            if(!myCrewmate.owner.equals(c.owner)) {
                shapeRenderer.rect(centerHP.x + (c.getX() - myCrewmate.getX()) * 2,
                        centerHP.y + (c.getY() - myCrewmate.getY()) * 2, 50 * (c.getHP() / c.getMaxHP()), 10);
                c.getLabel().setPosition(174 + (c.getX() - myCrewmate.getX()),
                        165 + (c.getY() - myCrewmate.getY()));
            }
        }

        myCrewmate.draw(game.batch);
        shapeRenderer.rect(centerHP.x ,centerHP.y, 50 * (myCrewmate.getHP() / myCrewmate.getMaxHP()), 10);
        myCrewmate.getLabel().setPosition(174, 166);

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
