package com.bu.zxinglibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.bj.qrcodelibrary.DecodeFormatManager;
import com.bu.zxinglibrary.BitmapLuminanceSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Description：解析工具类
 * Created by Buuu on 2017/12/15.
 */

public class Util {
    /**
     * 获取分辨率
     *
     * @param context context
     * @return Point
     */
    public static Point setDisplay(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int width = displaySize.x;
        int height = displaySize.y;
        return new Point(width, height);
    }

    /**
     * 获取压缩过的图片
     *
     * @return
     */
    public static Bitmap getBitmp(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //先算出测量bitmap大小
        options.inJustDecodeBounds = true;
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BitmapFactory.decodeFile(path, options);
        if (options.outWidth >= 300 || options.outHeight >= 300) {
            int size = Math.max(options.outWidth / 300, options.outHeight / 300);
            options.inSampleSize = size >= 1 ? size : 1;
        }
        options.inJustDecodeBounds = false;
        Bitmap dscBitmap = BitmapFactory.decodeFile(path, options);
        return dscBitmap;
    }

    /**
     * 获取结果
     *
     * @param bitmap 需要解析的bitmap
     * @return
     */
    public static String getResult(Bitmap bitmap) {
        //Zxing自带的解析类
        byte data[];
        int[] datas = new int[bitmap.getWidth() * bitmap.getHeight()];
        data = new byte[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(datas, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < datas.length; i++) {
            data[i] = (byte) datas[i];
        }
        Set<BarcodeFormat> decodeFormats = new HashSet<>();
        decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildLuminanceSource(data, bitmap.getWidth(), bitmap.getHeight());
        if (source != null) {
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap1);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }

        return rawResult != null ? rawResult.getText() : "";
    }

    public static PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = new Rect(0, 0, width, height);
        // Go ahead and assume it's YUV rather than die.
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                rect.width(), rect.height(), false);
    }

}
