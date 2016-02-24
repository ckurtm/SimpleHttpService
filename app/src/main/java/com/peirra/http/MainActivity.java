package com.peirra.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.peirr.http.mvp.HttpContract;
import com.peirr.http.mvp.HttpPresenter;
import com.peirr.http.mvp.HttpServer;
import com.peirr.http.mvp.IServerRequest;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;

public class MainActivity extends AppCompatActivity implements HttpContract.View {

    String TAG = MainActivity.class.getSimpleName();
    TextView message;
    HttpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (TextView) findViewById(R.id.message);

        IServerRequest server = new HttpServer(this, SimpleHttpService.generatePort());
        presenter = new HttpPresenter(server, this);

        Switch plug = (Switch) findViewById(R.id.switch1);
        plug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    presenter.bootup();
                } else {
                    presenter.shutdown();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.connect();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.disconnect();
    }

    @Override
    public void showHttpStatus(int status, SimpleHttpInfo info) {
        Log.d(TAG, "showHttpStatus() [state:" + status + "] [http://" + info.ip + ":" + info.port + "]");
        switch (status) {
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
}
