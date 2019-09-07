package example.asus.digimongo;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class connectServer {
    Socket socket;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean status;
    private Thread thread;
    String ip;

    public connectServer(String ip) {
        this.ip=ip;
        ServerConnect();
    }

    public void ServerConnect() {
        new Thread() {
            public void run() {
                try {
                    socket = new Socket(ip, 33356);
                    Log.d("[Client]", " Server connected !!");
                    is = socket.getInputStream();
                    dis = new DataInputStream(is);
                    os = socket.getOutputStream();
                    dos = new DataOutputStream(os);
                    try {
                        //String str = ip; // loign ip 전송
                        String str=ip;  //ip전송
                        byte[] bb;
                        bb = str.getBytes("ksc5601");
                        dos.write(bb); //.writeUTF(str);
                        //sendMsg(str1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    thread = new Thread(new ReceiveMsg());
                    thread.setDaemon(true);
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("[MultiChatClient]", " connectServer() Exception !!");
                }
            }
        }.start();
    }
    class ReceiveMsg implements Runnable {
        @SuppressWarnings("null")
        @Override
        public void run() {
            status = true;
            while (status) {
                try {
                    byte[] b = new byte[64];
                    dis.read(b);
                    String msg = new String(b, "euc-kr"); //+ "\n";
                    msg = msg.trim();
                    Log.d("789", msg);
                    ExecMsg(msg);

                } catch (IOException e) {
                    //e.printStackTrace();
                    //status = false;
                    try {
                        os.close();
                        is.close();
                        dos.close();
                        dis.close();
                        socket.close();
                        break;
                    } catch (IOException e1) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("[MultiChatClient]", " Stopped");
        }
    }


    public void ExecMsg(String msg) {
        String[] msgArr = msg.split("\t");
        String cmd = msgArr[0];
        Log.d("4449", cmd);
    }

}
