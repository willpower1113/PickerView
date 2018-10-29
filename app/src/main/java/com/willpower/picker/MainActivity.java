package com.willpower.picker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.willpower.picker.dialog.NoticeDialog;
import com.willpower.picker.window.FloatManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    PickerView pickerView;

    NoticeDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);
        FloatManager.getManager().setClickListener(new FloatManager.OnFloatWindowClickListener() {
            @Override
            public void onFloatWindowClick() {
                FloatManager.getManager().hideWindow();
            }
        });
//        dialog = new NoticeDialog(this);
//        dialog.setContentText("房间公告房间公告房房间公告t房间公告房间公告房房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告房间公告间公告房间公告房间公告房间公告");
        findViewById(R.id.clickLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FloatManager.getManager().checkPermission(MainActivity.this)) {
                    FloatManager.getManager().showWindow();
                    startActivity(new Intent(MainActivity.this, TestActivity.class));
                } else {
                    FloatManager.getManager().requestPermission(MainActivity.this);
                }

//                dialog.show();
            }
        });

        pickerView = findViewById(R.id.pickerView);
        pickerView.setFocusableChangeView(view);
        pickerView.setPlusDrawable(R.drawable.ic_plus, R.drawable.ic_plus_disable);
        pickerView.setReduceDrawable(R.drawable.ic_reduce, R.drawable.ic_reduce_disable);
        pickerView.setNumberDrawable(R.drawable.selector_edit_num);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FloatManager.getManager().updateText("当前时间：", new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis())));
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
