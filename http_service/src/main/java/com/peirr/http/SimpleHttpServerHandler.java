/*
 * Copyright (c) 2015. Peirr, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any means is strictly prohibited.
 * Proprietary and Confidential
 */

package com.peirr.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;


import com.peirr.http.utils.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

class SimpleHttpServerHandler extends Thread {
    String TAG = SimpleHttpServerHandler.class.getSimpleName();
    private Socket toClient;
    private String documentRoot;
    private Context context;
    private String html = "<html><body bgcolor=\"#000\" text=\"#fff\">{CONTENT}<body><html>";
    private final int BUFFER_SIZE = 1024;

    public SimpleHttpServerHandler(String d, Context c, Socket s) {
        toClient = s;
        documentRoot = d;
        context = c;
    }

    public void run() {
        String path = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
            // Receive data
            while (true) {
                String s = in.readLine().trim();
                if (s.equals("")) {
                    break;
                }
                if (s.substring(0, 3).equals("GET")) {
                    int leerstelle = s.indexOf(" HTTP/");
                    path = s.substring(5,leerstelle);
                    path = path.replaceAll("[/]+","/");
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"reading buffer: ", e);
            SimpleHttpServer.remove(toClient);
            try {
                toClient.close();
            }
            catch (Exception ex){
                Log.e(TAG,"error closing client: ", ex);
            }
        }
        get(path);
    }


    public void release(){
        if (toClient != null) {
            try {
                toClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void get(String path) {
        Log.d(TAG, "GET [path:" + path + "]");
        // Standard-Doc
        if (TextUtils.isEmpty(path)) {
            path = "index.html";
        }

        // Don't allow directory traversal
        if (path.contains("..")) {
            path = "403.html";
        }

        // Search for files in docroot
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "failed to decode path", e);
        }
        path = documentRoot + path;
        Log.d(TAG, "file: " + path);
        path = path.replaceAll("[/]+","/");

        if(path.charAt(path.length()-1) == '/') {
            path = documentRoot + "404.html";
        }

        String header = getHeaderBase(path);
        header = header.replace("%code%", "403 Forbidden");

        try {
            File f = new File(path);
            if (!f.exists()) {
                header = getHeaderBase(path);
                header = header.replace("%code%", "404 File not found");
                path = "404.html";
            }
        }
        catch (Exception e) {}
        if (!path.equals(documentRoot + "403.html")) {
            header = getHeaderBase(path).replace("%code%", "200 OK");
        }
        Log.d(TAG, "Serving " + path);
        try {
            File f = new File(path);
            if (f.exists()) { //TODO only allow access to files in the apps folders
                header = header.replace("%length%", "" + f.length());
                FileInputStream fis = new FileInputStream(f);
                OutputStream os = toClient.getOutputStream();
                os.write(header.getBytes());
                IO.bufferSize = BUFFER_SIZE; //TODO set this depending on the amount of RAM thats available to phone
                IO.copy(fis, os);
                fis.close();
            } else {
                // Send HTML-File (Ascii, not as a stream)
                header = getHeaderBase(path);
                header = header.replace("%code%", "404 File not found");
                header = header.replace("%length%", ""+get404().length());
                PrintWriter out = new PrintWriter(toClient.getOutputStream(), true);
                out.print(header);
                out.print(get404());
                out.flush();
            }
            SimpleHttpServer.remove(toClient);
            toClient.close();
        } catch (Exception e) {

        }
    }


    private String get404(){
        return html.replace("{CONTENT}","");
    }

    private String getHeaderBase(String path) {
        return  "HTTP/1.1 %code%\n"+
                "Content-Type: " + getMimeType(path) + "\n"+
                "X-Cache: HIT\n" +
                "Content-Length: %length%\n"+
                "Accept-Ranges: bytes\n" +
                "Cache-Control: no-cache\n" +
                "Pragma: no-cache\n" +
                "Content-Encoding: identity\n" +
                "Connection: close\n"+
                "Access-Control-Allow-Origin: *\n" + //TODO i should not allow any origin here, it should just be the server
                "SimpleHttpService: SimpleHttpService/1.0\n\n";
    }


    public String getMimeType(String url){
        String type = "text/html";
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (!TextUtils.isEmpty(extension)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
