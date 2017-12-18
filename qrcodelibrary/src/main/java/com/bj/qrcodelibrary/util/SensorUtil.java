package com.bj.qrcodelibrary.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Description： 感应器帮助类,例如闪光灯,感应亮度
 * Created by Buuu on 2017/12/14.
 */

public class SensorUtil {

    public static boolean hasFlashlight(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
