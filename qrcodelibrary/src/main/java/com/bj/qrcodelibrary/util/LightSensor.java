package com.bj.qrcodelibrary.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Description：亮度感应器
 * Created by Buuu on 2017/12/15.
 */

public class LightSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;

    public LightSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        assert sensorManager != null;
        if (sensorManager != null)
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    /**
     * 注册
     */
    public void register() {
        if (sensor != null && sensorManager != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 解除注册
     */
    public void unReigister() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (changeListener != null) {
            changeListener.onSensorChanged(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (changeListener != null) {
            changeListener.onAccuracyChanged(sensor, accuracy);
        }
    }

    public interface ChangeListener {
        void onSensorChanged(SensorEvent event);

        void onAccuracyChanged(Sensor sensor, int accuracy);
    }

    private ChangeListener changeListener;

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}
