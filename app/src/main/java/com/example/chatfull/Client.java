package com.example.chatfull;

import static com.example.chatfull.Utility.printLog;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, String> {

    ConnectToUserActivity activity;
    private String dstAddress, serverResponse = "";
    private int dstPort;
    private Socket clientSocket = null;
    User user;

    Client(String dstAddress, int dstPort, ConnectToUserActivity activity) {
        this.dstAddress = dstAddress;
        this.dstPort = dstPort;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {
            //Log.e("CLIENT", "Before Connection");
            printLog("[Client].doInBackground() Before Connection");
            printLog(String.format("[Client].doInBackground() Before Connection dstAddress ===>[%s], dstPort ===>[%d]", dstAddress, dstPort));
            clientSocket = new Socket(dstAddress, dstPort);

            if (clientSocket != null) {
                printLog("[Client].doInBackground() clientSocket is not null");

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(ShowInfoActivity.getSelfIpAddress() + ":" + ShowInfoActivity.getSelfPort() + "_" + DialogViewActivity.me.getName());

                //Log.e("CLIENT", "After Connection");
                printLog("[Client].doInBackground() After Connection");
                user = new User(dstAddress, dstPort);

                //MainActivity.userArrayList.add(user);
            }

            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                serverResponse = input.readLine();

                String connected_name = serverResponse.substring(serverResponse.indexOf('_')+1);

                printLog(String.format("[Client].doInBackground() Before Set name. connected_name ===>[%s], serverResponse ===>[%s] ", connected_name, serverResponse));
                user.setName(connected_name);
                user.setId(serverResponse);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("CLIENT", "Could not read socket");
            }
            printLog("[Client].doInBackground() Before Set user.");
            activity.setUser(user);

        } catch (Exception e) {
            e.printStackTrace();
            serverResponse = e.getCause().toString();
            printLog(String.format("[Client].doInBackground() Exception occurs. serverResponse ===>[%s] ", serverResponse));

        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    Log.e("CLIENT", "Could Not Close Client");
                    e.printStackTrace();
                }
            }
        }
        return serverResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        printLog(String.format("[Client].onPostExecute() Exception occurs. result ===>[%s] ", result));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.progressOverlay.setVisibility(View.INVISIBLE);
                Toast.makeText(activity.getApplicationContext(), serverResponse, Toast.LENGTH_LONG).show();
            }
        });
        super.onPostExecute(result);
    }
}
