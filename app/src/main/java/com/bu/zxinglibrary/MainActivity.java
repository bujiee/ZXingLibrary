package com.bu.zxinglibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bj.qrcodelibrary.CaptureActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void qrCodeScan(View v) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }

    public void qrCodeCreate(View v) {
        Intent intent = new Intent(this, CreateQRCodeActivity.class);
        startActivity(intent);
    }

    public void qrCodePicture(View v) {
        Intent intent = new Intent(this, PictureIdentificationActivity.class);
        startActivity(intent);
    }
}
