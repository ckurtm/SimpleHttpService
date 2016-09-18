package com.peirra.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.peirr.http.HttpContract;
import com.peirr.http.HttpPresenter;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;

public class MainActivity extends AppCompatActivity implements HttpContract.View {

    String TAG = MainActivity.class.getSimpleName();
    private TextView message;
    private Button button;
    private HttpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (TextView) findViewById(R.id.message);
        button = (Button) findViewById(R.id.button);

        presenter = new HttpPresenter(Injection.provideHttpRequest(this));

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (button.getText().equals("STOP")) {
                    presenter.stopService();
                } else {
                    presenter.startService();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.attachView(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        presenter.detachView();
    }


    @Override
    public void showHttpStatus(int status, SimpleHttpInfo info) {
        Log.d(TAG, "showHttpStatus() [state:" + status + "] [http://" + info.ip + ":" + info.port + "]");
        switch (status) {
            case SimpleHttpService.STATE_RUNNING:
                message.setText(info.ip + ":" + info.port);
                button.setText("STOP");
                break;
            case SimpleHttpService.STATE_STOPPED:
                message.setText("STATE_STOPPED [" + info.ip + ":" + info.port + "]");
                button.setText("START");
                break;
            case SimpleHttpService.STATE_ERROR:
                message.setText("STATE_ERROR");
                button.setText("STOP");
                break;
        }
    }
}
