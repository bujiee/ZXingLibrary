package com.bu.zxinglibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Description：ToastUtil
 * Created by Buuu on 2017/12/19.
 */

public class ToastUtil {
    private static Toast mToast;
    @SuppressLint("ShowToast")
    public static void showToast(Context context, CharSequence charSequence){
        if (mToast==null){
            mToast=Toast.makeText(context,charSequence,Toast.LENGTH_SHORT);
        }else {
            mToast.setText(charSequence);
        }
        mToast.show();
    }
}
