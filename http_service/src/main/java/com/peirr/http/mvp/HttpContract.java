package com.peirr.http.mvp;

import com.peirr.presentation.MvpPresenter;
import com.peirr.presentation.MvpView;
import com.peirr.http.service.SimpleHttpInfo;

/**
 * Created by kurt on 2015/11/24.
 */
public class HttpContract {

    public interface View extends MvpView {
        void showHttpStatus(int status,SimpleHttpInfo info);
    }

    public interface Presenter extends MvpPresenter<View> {
        void bootup();
        void shutdown();
        void info();
        void connect();
        void disconnect();
        void startService();
        void stopService();
    }

}
