package com.bu.zxinglibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.qrcodelibrary.encode.EncodeActivity;
import com.bj.qrcodelibrary.encode.QRCodeEncoder;
import com.bj.qrcodelibrary.encode.QRCodeEncoder2;
import com.bu.zxinglibrary.util.Util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.regex.Pattern;

/**
 * 生成二维码,根据实际情况修改界面
 */
public class CreateQRCodeActivity extends AppCompatActivity {
    private EditText et_input;
    private ImageView iv_image_view;
    private QRCodeEncoder qrCodeEncoder;
    private static final String TAG = EncodeActivity.class.getSimpleName();
    private static final int MAX_BARCODE_FILENAME_LENGTH = 24;
    private static final Pattern NOT_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final String USE_VCARD_KEY = "USE_VCARD";
    private QRCodeEncoder2 mQRCodeEncoder2;
    private int smallerDimension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        et_input = findViewById(R.id.et_input);
        iv_image_view = findViewById(R.id.iv_image_view);
        et_input.setOnKeyListener(textListener);
        Point point = Util.setDisplay(this);
        int width = point.x;
        int height = point.y;
        smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 7 / 8;
        mQRCodeEncoder2 = new QRCodeEncoder2(smallerDimension);
    }

    /*监控键盘回车键*/
    private final View.OnKeyListener textListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String text = ((TextView) view).getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    Intent intent = getIntent();
                    if (intent == null) {
                        return true;
                    }
                    try {
                        mQRCodeEncoder2 = new QRCodeEncoder2(smallerDimension);
                        //生成二维码
                        Bitmap bitmap = mQRCodeEncoder2.encodeAsBitmap(text, BarcodeFormat.QR_CODE);
                        if (bitmap == null) {
                            qrCodeEncoder = null;
                            return true;
                        }
                        iv_image_view.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        qrCodeEncoder = null;
                    }
                }
                return true;
            }
            return false;
        }
    };
}
