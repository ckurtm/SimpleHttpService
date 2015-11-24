package com.peirr.http.mvp;

import com.peirr.http.service.SimpleHttpInfo;

/**
 * Created by kurt on 2015/11/24.
 */
public class HttpContract {

    public interface View {
        void showHttpStatus(int status,SimpleHttpInfo info);
    }

    public interface ActionsListener {
        void bootup();
        void shutdown();
        void info();
        void connect();
        void disconnect();
    }

}
