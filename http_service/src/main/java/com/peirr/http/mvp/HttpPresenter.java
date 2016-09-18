package com.peirr.http.mvp;

import com.peirr.presentation.BasePresenter;
import com.peirr.http.service.ISimpleHttpServiceServer;
import com.peirr.http.service.SimpleHttpInfo;

/**
 * Created by kurt on 2015/11/24.
 */
public class HttpPresenter extends BasePresenter<HttpContract.View> implements HttpContract.Presenter, ISimpleHttpServiceServer {

    private final IServerRequest request;

    public HttpPresenter(IServerRequest request) {
        this.request = request;
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
        if(isViewAttached()) {
            getView().showHttpStatus(state, info);
        }
    }

    @Override
    public void onBoundServiceConnectionChanged(boolean connected) {
        if (connected) {
            request.info();
        }
    }

    @Override
    public void attachView(final HttpContract.View mvpView) {
        super.attachView(mvpView);
        connect();
    }

    @Override
    public void detachView() {
        super.detachView();
        disconnect();
    }
}
