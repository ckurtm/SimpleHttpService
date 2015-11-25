/*
 * Copyright (c) 2015. Peirr, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any means is strictly prohibited.
 * Proprietary and Confidential
 */

package com.peirr.http;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class SimpleHttpServer extends Thread {
    String TAG = SimpleHttpServer.class.getSimpleName();
    private ServerSocket listener = null;
    private boolean running = true;
    private String documentRoot;
    private static Handler handler;
    private Context context;
    private SimpleHttpServerHandler httpThread;
    public static LinkedList<Socket> clientList = new LinkedList<Socket>();

    public SimpleHttpServer(Handler handler, String documentRoot, String ip, int port, Context context) throws IOException {
        super();
        this.documentRoot = documentRoot;
        this.context = context;
        SimpleHttpServer.handler = handler;
        InetAddress ipadr = InetAddress.getByName(ip);
        listener = new ServerSocket(port, 0, ipadr);
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                send("Waiting for connections");
                Socket client = listener.accept();
                send("New connection from " + client.getInetAddress().toString());
                releaseHttpThread();
                httpThread = new SimpleHttpServerHandler(documentRoot, context, client);
                httpThread.start();
                clientList.add(client);
            } catch (IOException e) {
                send(e.getMessage());
                Log.w(TAG,"server shutdown ..: ");
            }
        }
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public void stopServer() {
        setRunning(false);
        try {
            for(Socket client:clientList){
                client.close();
            }
            releaseHttpThread();
            listener.close();
        } catch (IOException e) {
            send(e.getMessage());
            Log.e(TAG,"server shutdown error: " +  Log.getStackTraceString(e));
        }
    }


    private void releaseHttpThread(){
        if (httpThread != null) {
            httpThread.release();
            httpThread.interrupt();
        }
    }

    public synchronized static void remove(Socket s) {
        send("Closing connection: " + s.getInetAddress().toString());
        clientList.remove(s);
    }

    private static void send(String s) {
        if (s != null) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("msg", s);
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

}
