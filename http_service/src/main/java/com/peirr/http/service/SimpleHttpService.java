package com.peirr.http.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.peirr.http.SimpleHttpServer;

import java.lang.ref.WeakReference;
import java.security.SecureRandom;

import mbanje.kurt.remote_service.RemoteService;
import mbanje.kurt.remote_service.RemoteServiceType;

/**
 * Created by kurt on 2015/11/23.
 */
@RemoteService(RemoteServiceType.STARTED_BOUND)
public class SimpleHttpService extends Service implements ISimpleHttpServiceClient {

    String TAG = SimpleHttpService.class.getSimpleName();

    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOPPED = 2;
    public static final int STATE_ERROR = 3;

    public static final int REQUEST_START = 12;
    public static final int REQUEST_STOP = 14;
    public static final int REQUEST_INFO = 16;

    private String ip;
    private int port;
    private SimpleHttpServiceConnector connector;
    private SimpleHttpServer server;
    private String serverRoot = "";
    private Handler serverHandler = new ServerHandler(this);
    private SecureRandom random = new SecureRandom();
    private int currentState = STATE_STOPPED;


    public SimpleHttpService() {
        this.connector = new SimpleHttpServiceConnector(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connector.getBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        port = randomPort(9960,9999);
    }

    @Override
    public void bootup(int port) {
        Log.d(TAG,"bootup()");
        try{
            if(port != 0){
                this.port = port;
            }
            SimpleHttpInfo info = getInfo();
            server = new SimpleHttpServer(serverHandler, serverRoot,info.ip,info.port,getApplicationContext());
            server.start();
            currentState = STATE_RUNNING;
            Log.d(TAG,"boot success...");
        } catch (Exception e) {
            currentState = STATE_ERROR;
            Log.d(TAG,"boot failure...");
            Log.e(TAG, Log.getStackTraceString(e));
        }
        connector.send(currentState,new SimpleHttpInfo(ip,port));
    }

    @Override
    public void shutdown() {
        Log.d(TAG,"shutdown()");
        currentState = STATE_STOPPED;
        if(server != null){
            server.stopServer();
        }
        connector.send(STATE_STOPPED, getInfo());
        stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(currentState == STATE_RUNNING){
            shutdown();
        }
    }

    @Override
    public void info(int port) {
        if(port != 0){
            this.port = port;
        }
        Log.d(TAG,"info()");
        connector.send(currentState, getInfo());
    }

    public String getIp() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
            return null;
        }
        return intToIp(wifiInfo.getIpAddress());
    }


    private SimpleHttpInfo getInfo(){
        if (TextUtils.isEmpty(ip)) {
            ip = getIp();
        }
        return new SimpleHttpInfo(ip, port);
    }

    private String intToIp(int i) {
        return ((i) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }


    private static class ServerHandler extends Handler {
        final WeakReference<SimpleHttpService> reference;

        private ServerHandler(SimpleHttpService service) {
            this.reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private int randomPort(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
