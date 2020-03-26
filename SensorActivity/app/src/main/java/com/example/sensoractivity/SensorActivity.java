package com.example.sensoractivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorActivity extends Activity implements SensorEventListener {
    TextView infos_acc;
    TextView infos_gyro;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        infos_acc = findViewById(R.id.info_acc);
        infos_gyro = findViewById(R.id.info_gyro);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mGyroscope,SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            StringBuilder sb = new StringBuilder("x: "+x+" y: "+y+" z: "+z);
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                infos_acc.setText(new StringBuilder("Accelerometer: "+sb));
            } else {
                infos_gyro.setText(new StringBuilder("Gyroscope: "+sb));
            }
        }
    }

}
