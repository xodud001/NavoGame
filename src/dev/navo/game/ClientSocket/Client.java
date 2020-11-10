package dev.navo.game.ClientSocket;

import dev.navo.game.Tools.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@SuppressWarnings("unchecked")
public class Client {

    private static Client instance;
    private Socket clientSocket;
    BufferedReader in;
    PrintWriter out;

    String owner;

    String serverIPv4 = "127.0.0.1";
    int serverPort = 10002;



    public static Client getInstance(){
        if(instance == null)
            instance = new Client();
        return instance;
    }

    public Client() {
        connect();
        streamSetting();
        //dataSend();
        //dataRecv();
    }    

    public void connect() {
        try{
            clientSocket=new Socket(serverIPv4, serverPort);
            System.out.println("[Client] Connected to server");
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public void streamSetting() {
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }


    public void dataRecv() {
        new Thread(new Runnable() {
            boolean isThread = true;
            @Override
            public void run() {
                while(isThread)
                    try {
                        String recvData=in.readLine();
                        if(recvData.equals("/quit"))
                            isThread=false;
                        else
                            System.out.println("Server : " + recvData);
                    }
                    catch (Exception e) {
                        System.out.println(e.toString());
                        try {
                            if(clientSocket != null)
                                clientSocket.close();
                            in.close();
                        } catch (IOException ioE) {
                            ioE.printStackTrace();
                        }
                        isThread = false;
                    }
            }
        }).start();
    }

    public void dataSend() {
        new Thread(new Runnable() {
            Scanner input=new Scanner(System.in);
            boolean isThread=true;
            @Override
            public void run() {
                while(isThread)
                    try {
                        String sendData=input.nextLine();
                        if(sendData.equals("/quit"))
                            isThread=false;
                        else {
                            //String login = String.format("{\"Header\":%s,\"id\":%s,\"pw\":%s}", header, id, pw);
                            //System.out.println(login);
                            out.println(sendData);
                        }
                    }
                    catch (Exception e) {
                        System.out.println(e.toString());
                        try {
                            if(clientSocket != null)
                                clientSocket.close();
                            out.close();
                        } catch (IOException ioE) {
                            ioE.printStackTrace();
                        }
                        isThread = false;
                    }
            }
        }).start();
    }

    public boolean login(String id, String pw) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "LOGIN");
        json.put("id", id);
        json.put("pw", pw);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());
        String recvData= in.readLine();

        return recvData.equals("SUCCESS");
    }

    public boolean create(String id, String pw, String name, String birth, String phone) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "CREATE");
        json.put("id", id);
        json.put("pw", pw);
        json.put("name", name);
        json.put("birth", birth);
        json.put("phone", phone);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String recvData= in.readLine();

        return recvData.equals("SUCCESS");
    }

    public String idFind(String name, String birth) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "ID");
        json.put("name", name);
        json.put("birth", birth);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String result = in.readLine();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }

    public String pwFind(String id, String name) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "PW");
        json.put("id", id);
        json.put("name", name);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String result = in.readLine();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }

    public void logout(){
        JSONObject json = new JSONObject();
        json.put("Header", "LOGOUT");

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());
    }

    public JSONObject enter() throws IOException, ParseException {
        JSONObject json = new JSONObject();
        json.put("Header", "ENTER");
        json.put("owner", this.owner);


        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String result = in.readLine();
        return JsonParser.createJson(result);
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}


