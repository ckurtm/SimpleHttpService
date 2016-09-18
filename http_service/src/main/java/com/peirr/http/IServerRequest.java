package com.peirr.http;

import com.peirr.http.service.ISimpleHttpServiceServer;

/**
 * Created by kurt on 2015/11/24.
 */
public interface IServerRequest {
    void startService();
    void stopService();
    void bootup();
    void shutdown();
    void info();
    void setListener(ISimpleHttpServiceServer listener);
    void connect();
    void disconnect();
}
