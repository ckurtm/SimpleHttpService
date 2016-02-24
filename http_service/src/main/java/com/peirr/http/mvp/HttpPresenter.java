package com.peirr.http.mvp;

import com.peirr.http.service.ISimpleHttpServiceServer;
import com.peirr.http.service.SimpleHttpInfo;

/**
 * Created by kurt on 2015/11/24.
 */
public class HttpPresenter implements HttpContract.ActionsListener, ISimpleHttpServiceServer {

    private final IServerRequest repository;
    private final HttpContract.View view;

    public HttpPresenter(IServerRequest repository, HttpContract.View view) {
        this.repository = repository;
        this.view = view;
        repository.setListener(this);
    }

    @Override
    public void bootup() {
        repository.bootup();
    }

    @Override
    public void shutdown() {
        repository.shutdown();
    }

    @Override
    public void info() {
        repository.info();
    }

    @Override
    public void connect() {
        repository.connect();
    }

    @Override
    public void disconnect() {
        repository.disconnect();
    }

    @Override
    public void onHttpServerStateChanged(int state, SimpleHttpInfo info) {
        view.showHttpStatus(state, info);
    }

    @Override
    public void onBoundServiceConnectionChanged(boolean connected) {
        if (connected) {
            repository.info();
        }
    }
}
