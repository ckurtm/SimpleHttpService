package com.peirra.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.ISimpleHttpServiceServer;
import com.peirr.http.service.SimpleHttpService;
import com.peirr.http.service.SimpleHttpServiceClient;

public class MainActivity extends AppCompatActivity implements ISimpleHttpServiceServer {

    String TAG = MainActivity.class.getSimpleName();
    SimpleHttpServiceClient http;

    int PORT = SimpleHttpService.generatePort();
    TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (TextView) findViewById(R.id.message);

        Switch plug = (Switch) findViewById(R.id.switch1);
        plug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    http.bootup(PORT);
                }else{
                    http.shutdown();
                }
            }
        });

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                http.info(PORT);
            }
        });


        Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                http.shutdown();
            }
        });


        http = SimpleHttpServiceClient.createStub(this,this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        http.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        http.disconnect();
    }

    @Override
    public void onHttpServerStateChanged(int state, SimpleHttpInfo info) {
        Log.d(TAG,"onHttpServerStateChanged() [state:"+state+"] [info:"+info+"]");
        switch (state){
            case SimpleHttpService.STATE_RUNNING:
                message.setText(info.ip + ":" + info.port);
                break;
            case SimpleHttpService.STATE_STOPPED:
                message.setText("STATE_STOPPED [" + info.ip + ":" + info.port + "]");
                break;
            case SimpleHttpService.STATE_ERROR:
                message.setText("STATE_ERROR");
                break;

        }

    }

    @Override
    public void onBoundServiceConnectionChanged(boolean connected) {
      message.setText("SERVICE_" + (connected ? "CONNECTED" : "DISCONNECTED"));
    }
}
