package com.peirr.http.mvp;

import com.peirr.http.service.ISimpleHttpServiceServer;
import com.peirr.http.service.SimpleHttpInfo;

/**
 * Created by kurt on 2015/11/24.
 */
public class HttpPresenter implements HttpContract.ActionsListener, ISimpleHttpServiceServer {

    private final IServerRequest request;
    private final HttpContract.View view;

    public HttpPresenter(IServerRequest request, HttpContract.View view) {
        this.request = request;
        this.view = view;
        request.setListener(this);
    }

    @Override
    public void bootup() {
        request.bootup();
    }

    @Override
    public void shutdown() {
        request.shutdown();
    }

    @Override
    public void info() {
        request.info();
    }

    @Override
    public void connect() {
        request.connect();
    }

    @Override
    public void disconnect() {
        request.disconnect();
    }

    @Override
    public void startService() {
        request.startService();
    }

    @Override
    public void stopService() {
        request.stopService();
    }

    @Override
    public void onHttpServerStateChanged(int state, SimpleHttpInfo info) {
        view.showHttpStatus(state, info);
    }

    @Override
    public void onBoundServiceConnectionChanged(boolean connected) {
        if (connected) {
            request.info();
        }
    }
}
