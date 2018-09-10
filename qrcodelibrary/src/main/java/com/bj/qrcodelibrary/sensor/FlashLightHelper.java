package com.bj.qrcodelibrary.sensor;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bj.qrcodelibrary.util.SensorUtil;

/**
 * Function:FlashLightHelper
 * 用于管理扫描页面的手电筒开关
 * Author  :@author BuJie
 * Date:2018/9/10
 */
public class FlashLightHelper implements CompoundButton.OnCheckedChangeListener {
    private final String TAG = "FlashLightHelper";
    private Camera mCamera;
    private CheckBox cb_flashlight;
    private Context mContext;

    public FlashLightHelper(Camera camera, CheckBox checkBox, Context context) {
        if (camera == null) {
            throw new NullPointerException("相机类为空");
        }
        if (checkBox == null) {
            throw new NullPointerException("checkBox不能为空");
        }
        if (context == null) {
            throw new NullPointerException("context不能为空");
        }
        this.mCamera = camera;
        this.cb_flashlight = checkBox;
        this.mContext = context;
        cb_flashlight.setOnCheckedChangeListener(this);
    }

    public void register(SensorEvent sensorEvent, int defaultValue) {
        if (mCamera == null)
            return;

        if (!SensorUtil.hasFlashlight(mContext)) {
            Log.d(TAG, "手机未发现手电筒");
            return;
        }

        if (sensorEvent == null)
            return;

        if (sensorEvent.values[0] <= (defaultValue >= 1 ? defaultValue : 50)) {
            cb_flashlight.setVisibility(View.VISIBLE);
        } else {
            //如果灯亮这，则不隐藏该按钮
            if (cb_flashlight.isChecked()) {
                if (cb_flashlight.getVisibility() != View.VISIBLE) {
                    cb_flashlight.setVisibility(View.VISIBLE);
                }
            } else {
                if (cb_flashlight.getVisibility() != View.GONE) {
                    cb_flashlight.setVisibility(View.GONE);
                }
            }
        }
    }

    public void onPause() {
        if (mCamera == null) {
            return;
        }
        cb_flashlight.setChecked(false);
        Camera.Parameters parameters = mCamera.getParameters();
        if (SensorUtil.hasFlashlight(mContext)) {
            if (!Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mCamera == null)
            return;
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        if (isChecked) {
            if (Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
        } else {
            if (!Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
        }
        mCamera.setParameters(parameters);
    }


    public void onResume() {
        //empty implement
    }

    public void onDestroy() {
        //empty implement
    }
}
