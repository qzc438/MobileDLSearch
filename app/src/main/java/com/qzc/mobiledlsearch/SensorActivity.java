package com.qzc.mobiledlsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

import java.util.List;

public class SensorActivity extends AppCompatActivity {

    private TextView tv_sensor;
    private TextView tv_sensor_xyz;

    private Sensor sensor;
    private float x, y, z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void init() {
        tv_sensor = (TextView) this.findViewById(R.id.tv_sensor);
        // scroll tv_sensor
        tv_sensor.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_sensor_xyz = (TextView) this.findViewById(R.id.tv_sensor_xyz);

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);
        tv_sensor.setText("This mobile phone has" + " [" + allSensors.size() + "] " + "sensors." + "They are：\n\n");

        // show details of the sensor
        for (Sensor s : allSensors) {
            String temp = "\n" + "Sensor Name: " + s.getName() + "\n" + "Sensor Version: " + s.getVersion() + "\n" + "Sensor Provider: " + s.getVendor() + "\n\n";
            switch (s.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + "Accelerometer: " + temp);
                    break;
                case Sensor.TYPE_GRAVITY:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Gravity: " + temp);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Gyroscope:" + temp);
                    break;
                case Sensor.TYPE_LIGHT:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Light:" + temp);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Linear Acceleration:" + temp);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Magnetic Field:" + temp);
                    break;
                case Sensor.TYPE_ORIENTATION:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Orientation:" + temp);
                    break;
                case Sensor.TYPE_PRESSURE:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Pressure:" + temp);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Proximity:" + temp);
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Rotation Vector:" + temp);
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Temperature:" + temp);
                    break;
                default:
                    tv_sensor.setText(tv_sensor.getText().toString() + s.getType()
                            + " Unknown:" + temp);
                    break;
            }
        }

        // show the change of accelerometer
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener lsn = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent e) {
                // TODO Auto-generated method stub
                x = e.values[SensorManager.DATA_X];
                y = e.values[SensorManager.DATA_Y];
                z = e.values[SensorManager.DATA_Z];
                tv_sensor_xyz.setText("x=" + (int) x + "," + "y=" + (int) y + "," + "z="+ (int) z + "\n");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub
            }
        };
        // register listener，third parameter is used to measure accuracy
        sm.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
}