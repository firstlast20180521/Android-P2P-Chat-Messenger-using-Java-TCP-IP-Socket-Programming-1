package com.example.chatfull;

import static com.example.chatfull.Utility.printLog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.glxn.qrgen.android.QRCode;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ShowInfoActivity extends AppCompatActivity {

    private static final int selfPort = 8080;
    private Server myServer;

    public void setConnected(User user) {
        Intent data = new Intent();
        data.putExtra("user",user);
        setResult(RESULT_OK,data);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        TextView ipView = findViewById(R.id.ipDisplay);
        TextView portView = findViewById(R.id.portDisplay);

        String ip_address = getSelfIpAddress();
        ipView.setText(ip_address);
        portView.setText(Integer.toString(selfPort));

        Bitmap myBitmap = QRCode.from(ip_address+":"+selfPort).bitmap();
        ImageView myImage = (ImageView) findViewById(R.id.qr_view);
        myImage.setImageBitmap(myBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myServer = new Server(this, getSelfIpAddress(), getSelfPort());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myServer != null)
            myServer.onDestroy();
    }

    public static int getSelfPort() {
        return selfPort;
    }

    // Returns device IP Address
    public static String getSelfIpAddress() {
        printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 1. Start of Method"));

        String self_ip_1 = "";
        String self_ip_temp = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            L1: while (enumNetworkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();

                printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 2. Interface Name ===>[%S]", networkInterface.getName()));

                if (networkInterface.isUp()) {
                    printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 3.1 is Up"));

                    Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                    while (enumInetAddress.hasMoreElements()) {
                        InetAddress inetAddress = enumInetAddress.nextElement();

                        if (inetAddress.isSiteLocalAddress()) {
                            printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 4.1 is SiteLocalAddress"));

                            self_ip_1 = inetAddress.getHostAddress();
                            printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 5.1 ip address ===>[%S]", self_ip_1));
                            //break L1;

                        }else {
                            printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 4.2 is not SiteLocalAddress"));

                            self_ip_temp = inetAddress.getHostAddress();
                            printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 5.2 ip address ===>[%S]", self_ip_temp));

                        }
                    }

                } else {
                    printLog(String.format("[ShowInfoActivity].getSelfIpAddress() 3.2 is Down"));

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
            // Log.e("GET_IP", "IP NOT FOUND");
            printLog(String.format("[ShowInfoActivity].getSelfIpAddress() ---ERROR--- IP NOT FOUND"));
        }

        printLog(String.format("[ShowInfoActivity].getSelfIpAddress() ---6---[%S]", self_ip_1));
        return self_ip_1;

    }
}
