package com.bu.zxinglibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bu.zxinglibrary.util.ImageUtil;
import com.bu.zxinglibrary.util.Util;

/**
 * 识别相册中的二维码图片
 */
public class PictureIdentificationActivity extends AppCompatActivity {
    private ImageView iv_image;
    private TextView tv_result;
    private final int IMAGE_CODE = 0X100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_identification);
        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_CODE);
            }
        });
        iv_image = findViewById(R.id.iv_image);
        tv_result = findViewById(R.id.tv_result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE) {
            if (data != null) {
                Uri uri = data.getData();
                String path = ImageUtil.getImageAbsolutePath(this, uri);
                Bitmap bitmap = Util.getBitmp(path);
                iv_image.setImageBitmap(bitmap);
                tv_result.setText(TextUtils.isEmpty(Util.getResult(bitmap)) ? "未发现可识别信息" : Util.getResult(bitmap));
            }
        }
    }
}
