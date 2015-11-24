package com.peirra.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;
import com.peirr.http.mvp.HttpContract;
import com.peirr.http.mvp.HttpPresenter;
import com.peirr.http.mvp.HttpRepositories;
import com.peirr.http.mvp.HttpRepository;

public class MainActivity extends AppCompatActivity implements HttpContract.View {

    String TAG = MainActivity.class.getSimpleName();
    TextView message;
    HttpPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (TextView) findViewById(R.id.message);

        HttpRepository repository = new HttpRepositories(this,SimpleHttpService.generatePort());
        presenter = new HttpPresenter(repository,this);

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

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.info();
            }
        });


        Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.shutdown();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.disconnect();
    }

    @Override
    public void showHttpStatus(int status, SimpleHttpInfo info) {
        Log.d(TAG,"showHttpStatus() [state:"+status+"] [info:"+info+"]");
        switch (status){
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
