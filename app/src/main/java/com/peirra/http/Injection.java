package com.peirra.http;

import android.app.Activity;

import com.peirr.http.HttpServer;
import com.peirr.http.IServerRequest;
import com.peirr.http.service.SimpleHttpService;

/**
 * Created by kurt on 2016/09/15.
 */

public class Injection {

    public static IServerRequest provideHttpRequest(Activity activity){
        return new HttpServer(activity, SimpleHttpService.generatePort());
    }
}
