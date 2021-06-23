package com.sxsdk.supersdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sxsdk.collection.CollectionManager;
import com.sxsdk.collection.callback.InfoCallBack;

public class MainActivity extends AppCompatActivity {

    private String[] mustPermissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(mustPermissions, 1001);
        }

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionManager.getLocationInfo(MainActivity.this, new InfoCallBack() {
                    @Override
                    public void result(String s) {
                        Log.e("TAG", "Location:" + s);
                    }
                });

                CollectionManager.getAllContacts(MainActivity.this, new InfoCallBack() {
                    @Override
                    public void result(String s) {
                        Log.e("TAG", "AllContacts:" + s);
                    }
                });

                CollectionManager.getSmsInfoList(MainActivity.this, new InfoCallBack() {
                    @Override
                    public void result(String s) {
                        Log.e("TAG", "SmsInfo:" + s);
                    }
                });

                CollectionManager.getAppInfoList(MainActivity.this, new InfoCallBack() {
                    @Override
                    public void result(String s) {
                        Log.e("TAG", "AppInfo:" + s);
                    }
                });

                CollectionManager.getCalendarInfo(MainActivity.this, new InfoCallBack() {
                    @Override
                    public void result(String s) {
                        Log.e("TAG", "CalendarInfo:" + s);
                    }
                });
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
