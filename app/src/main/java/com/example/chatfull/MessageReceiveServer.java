package com.example.chatfull;

import static com.example.chatfull.Utility.printLog;

import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiveServer {
    private String ip_address;
    private int port;
    ChatActivity activity;
    private ServerSocket serverSocket;
    private boolean stop = false;

    MessageReceiveServer(String ip_address, int port, ChatActivity activity) {
        this.ip_address = ip_address;
        this.port = port;
        this.activity = activity;

        printLog("[MessageReceiveServer].MessageReceiveServer() ");
        printLog(String.format("ip_address:[%s], port:[%d]", ip_address, port));

        Thread socketServerThread = new Thread(new MessageSocketServerThread());
        socketServerThread.start();
    }

    private class MessageSocketServerThread extends Thread {
        @Override
        public void run() {
            try {
                printLog(String.format("[MessageReceiveServer.MessageSocketServerThread].run() port:[%d]", port));

                serverSocket = new ServerSocket(port);
                while (stop == false) {
                    Socket received_userSocket = serverSocket.accept();
                    //Log.e("RECEIVE", "Connected");
                    printLog("[MessageReceiveServer.MessageSocketServerThread].run() connected");
                    //printLog(String.format("[MessageReceiveServer.MessageSocketServerThread].run() ipaddress ===>[%s]", serverSocket.));

                    try {
                        ObjectInputStream in = new ObjectInputStream(received_userSocket.getInputStream());
                        Message message = (Message) in.readObject();

                        //Log.e("RECEIVE", "RECEIVED ==>" + message);
                        if (true == message.isFile()) {
                            printLog(String.format("[MessageReceiveServer.MessageSocketServerThread].run() 受信メッセージ:[%s]", message.getFilename()));
                        } else if (true == message.isImage()) {
                            printLog(String.format("[MessageReceiveServer.MessageSocketServerThread].run() 受信メッセージ:[%s]", message.getImageUrl()));
                        } else {
                            printLog(String.format("[MessageReceiveServer.MessageSocketServerThread].run() 受信メッセージ:[%s]", message.getText().toString()));
                        }


                        activity.setMessage(message);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                stop = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
