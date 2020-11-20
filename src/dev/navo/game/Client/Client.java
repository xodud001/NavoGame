package dev.navo.game.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
@SuppressWarnings("unchecked")
public class Client {

    private static Client instance;

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "5001"));

    Channel channel;
    Buffer buffer;

    Buffer inputBuffer;

    String owner; //로그인 한 아이디
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getOwner() {
        return this.owner;
    }

    public Client(){
        buffer = new Buffer();
        inputBuffer = new Buffer();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer(buffer));

            channel = bootstrap.connect(HOST, PORT).sync().channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
        finally {
            System.out.println("설정 종료");
        }
    }

    public static Client getInstance(){
        if(instance == null)
            instance = new Client();
        return instance;
    }
    //로그인
    public boolean login(String id, String pw) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "LOGIN");
        json.put("id", id);
        json.put("pw", pw);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        String recvData = buffer.get();
        return recvData.equals("SUCCESS");
    }
    //회원가입
    public boolean create(String id, String pw, String name, String birth, String phone) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "CREATE");
        json.put("id", id);
        json.put("pw", pw);
        json.put("name", name);
        json.put("birth", birth);
        json.put("phone", phone);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        String recvData= buffer.get();
        return recvData.equals("SUCCESS");
    }
    //아이디 찾기
    public String idFind(String name, String birth) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "ID");
        json.put("name", name);
        json.put("birth", birth);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");

        String result = buffer.get();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }

    //패스워드 찾기
    public String pwFind(String id, String name) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "PW");
        json.put("id", id);
        json.put("name", name);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");

        String result = buffer.get();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }
}

