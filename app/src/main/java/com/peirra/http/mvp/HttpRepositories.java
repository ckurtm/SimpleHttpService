package com.peirra.http.mvp;

import android.app.Activity;

import com.peirr.http.service.ISimpleHttpServiceServer;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpServiceClient;

/**
 * Created by kurt on 2015/11/24.
 */
public class HttpRepositories implements HttpRepository,ISimpleHttpServiceServer {

    private final Activity activity;
    private SimpleHttpServiceClient http;
    private ISimpleHttpServiceServer listener;
    private final int port;

    public HttpRepositories(Activity activity,int port) {
        this.activity = activity;
        this.port = port;
        http =  SimpleHttpServiceClient.createStub(activity,this);
    }

    @Override
    public void bootup() {
        http.bootup(port);
    }

    @Override
    public void shutdown() {
        http.shutdown();
    }

    @Override
    public void info() {
        http.info(port);
    }

    @Override
    public void connect() {
        http.connect();
    }

    @Override
    public void disconnect() {
        http.disconnect();
    }

    @Override
    public void onHttpServerStateChanged(int state, SimpleHttpInfo info) {
        if (listener != null) {
            listener.onHttpServerStateChanged(state,info);
        }
    }

    @Override
    public void onBoundServiceConnectionChanged(boolean connected) {
        if (listener != null) {
            listener.onBoundServiceConnectionChanged(connected);
        }
    }

    public void setListener(ISimpleHttpServiceServer listener) {
        this.listener = listener;
    }
}