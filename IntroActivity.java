package com.facec.cksalstl.MediaReview;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import com.facec.cksalstl.MediaReview.common.HttpClient;
import com.facec.cksalstl.MediaReview.common.URLRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntroActivity extends AppCompatActivity {
    private ArrayList<Map<String, Object>> list = null;

    private long startTime = 0L;
    private long endTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        startTime = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://52.78.17.170:8080/Practice/facec.jsp";
                URLRequest req = new URLRequest(url);
                HttpClient client = new HttpClient();
                String result = client.get(req);
                if(result != null) {
                    ObjectMapper om = new ObjectMapper();
                    try {
                        list = om.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
                        handler.sendEmptyMessage(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                endTime = System.currentTimeMillis();

                if(endTime - startTime > 1400) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("info", list);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    handler.sendEmptyMessageDelayed(0, 100);
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    };

}
