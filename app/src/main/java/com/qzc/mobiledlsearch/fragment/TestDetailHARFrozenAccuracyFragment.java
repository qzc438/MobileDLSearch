package com.qzc.mobiledlsearch.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ornach.nobobutton.NoboButton;
import com.qzc.mobiledlsearch.HARFrozenClassifier;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.utils.ToastUtil;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

public class TestDetailHARFrozenAccuracyFragment extends Fragment implements SensorEventListener{

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private ImageView btn_back;

    // real-time testing
    private Spinner spn_activity_list;
    private EditText etxt_attempts;
    private Chronometer timer;
    private NoboButton btn_start_test;
    private NoboButton btn_resume_test;
    private NoboButton btn_stop_test;
    private CircleProgressView circleView_test_accuracy;
    private TextView txt_test_accuracy;
    private TextView txt_test_attempts;
    private ScrollView scrollLogs;
    private TextView textLogs;

    private List<String> resultList = new ArrayList<>();

    // deep learning
    private static final int N_SAMPLES = 100;
    // max position
    int idx = -1;

    private static List<Float> ax;
    private static List<Float> ay;
    private static List<Float> az;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;

    private static List<Float> ma;
    private static List<Float> ml;
    private static List<Float> mg;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;
    // only true then collect data from sensors
    private boolean mIsSensorUpdateEnabled = false;

    private float[] results;
    private HARFrozenClassifier classifier;

    private String[] labels = {"Biking", "Downstairs", "Jogging", "Sitting", "Standing", "Upstairs", "Walking"};
    private String selectedLabel = "Sitting";


    public static TestDetailHARFrozenAccuracyFragment createFor(String text) {
        TestDetailHARFrozenAccuracyFragment fragment = new TestDetailHARFrozenAccuracyFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_detail_har_accuracy, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);

        // show backwards
        btn_back = view.findViewById(R.id.btn_back);
        // back function
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        txt_test_accuracy = view.findViewById(R.id.txt_test_accuracy);
        txt_test_attempts = view.findViewById(R.id.txt_test_attempts);
        circleView_test_accuracy = view.findViewById(R.id.circleView_test_accuracy);
        spn_activity_list = view.findViewById(R.id.spn_activity_list);
        etxt_attempts = view.findViewById(R.id.etxt_attempts);
        timer = view.findViewById(R.id.txt_real_time);
        scrollLogs = view.findViewById(R.id.scrollLogs);
        textLogs = view.findViewById(R.id.textLogs);

        List<CharSequence> dataActivity  = new ArrayList<CharSequence>();
        dataActivity.add("Biking");
        dataActivity.add("Downstairs");
        dataActivity.add("Jogging");
        dataActivity.add("Sitting");
        dataActivity.add("Standing");
        dataActivity.add("Upstairs");
        dataActivity.add("Walking");

        ArrayAdapter<CharSequence> adapterActivity = new ArrayAdapter<CharSequence>(TestDetailHARFrozenAccuracyFragment.this.getActivity(),
                R.layout.my_spinner, dataActivity);
        adapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_activity_list.setAdapter(adapterActivity);
        spn_activity_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLabel = labels[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_start_test = view.findViewById(R.id.btn_start_test);
        btn_start_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultList.clear();
                // start sensor
                startSensors();
                // remove log
                textLogs.setText("");
                // start timer
                timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                timer.start();
            }
        });

        btn_resume_test = view.findViewById(R.id.btn_resume_test);
        btn_resume_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btn_resume_test.getText().equalsIgnoreCase("Resume")){
                    stopSensors();
                    timer.stop();
                    btn_resume_test.setText("Continue");
                }else{
                    startSensors();
                    timer.start();
                    btn_resume_test.setText("Resume");
                }
            }
        });

        btn_stop_test = view.findViewById(R.id.btn_stop_test);
        btn_stop_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultList.clear();
                stopSensors();
                timer.stop();
            }
        });

        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();
        ma = new ArrayList<>(); ml = new ArrayList<>(); mg = new ArrayList<>();

        mSensorManager = (SensorManager) TestDetailHARFrozenAccuracyFragment.this.getActivity().getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        classifier = new HARFrozenClassifier(TestDetailHARFrozenAccuracyFragment.this.getActivity().getApplicationContext());

    }

    private void startSensors(){
        if (mAccelerometer!=null && mLinearAcceleration!=null && mGyroscope!=null){
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
            mIsSensorUpdateEnabled =true;
        }else{
            ToastUtil.showText(TestDetailHARFrozenAccuracyFragment.this.getActivity(), "Not all required sensor is detected. The result may inaccurate.");
        }
    }

    private void stopSensors(){
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mLinearAcceleration);
        mSensorManager.unregisterListener(this, mGyroscope);
        mIsSensorUpdateEnabled =false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (!mIsSensorUpdateEnabled) {
            stopSensors();
            Log.e("Sensor", "Sensor Update disabled. returning");
            return;
        }

        activityPrediction();

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);

        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lx.add(event.values[0]);
            ly.add(event.values[1]);
            lz.add(event.values[2]);

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void activityPrediction() {

        List<Float> data = new ArrayList<>();

        if (ax.size() >= N_SAMPLES && ay.size() >= N_SAMPLES && az.size() >= N_SAMPLES
                && lx.size() >= N_SAMPLES && ly.size() >= N_SAMPLES && lz.size() >= N_SAMPLES
                && gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES
        ) {

            if (resultList.size() < Integer.parseInt(etxt_attempts.getText().toString()))
            {
                double maValue; double mgValue; double mlValue;

                for( int i = 0; i < N_SAMPLES ; i++ ) {
                    maValue = Math.sqrt(Math.pow(ax.get(i), 2) + Math.pow(ay.get(i), 2) + Math.pow(az.get(i), 2));
                    mlValue = Math.sqrt(Math.pow(lx.get(i), 2) + Math.pow(ly.get(i), 2) + Math.pow(lz.get(i), 2));
                    mgValue = Math.sqrt(Math.pow(gx.get(i), 2) + Math.pow(gy.get(i), 2) + Math.pow(gz.get(i), 2));

                    ma.add((float)maValue);
                    ml.add((float)mlValue);
                    mg.add((float)mgValue);
                }

                data.addAll(ax.subList(0, N_SAMPLES));
                data.addAll(ay.subList(0, N_SAMPLES));
                data.addAll(az.subList(0, N_SAMPLES));

                data.addAll(lx.subList(0, N_SAMPLES));
                data.addAll(ly.subList(0, N_SAMPLES));
                data.addAll(lz.subList(0, N_SAMPLES));

                data.addAll(gx.subList(0, N_SAMPLES));
                data.addAll(gy.subList(0, N_SAMPLES));
                data.addAll(gz.subList(0, N_SAMPLES));

                data.addAll(ma.subList(0, N_SAMPLES));
                data.addAll(ml.subList(0, N_SAMPLES));
                data.addAll(mg.subList(0, N_SAMPLES));

                results = classifier.predictProbabilities(toFloatArray(data));

                // find the max position
                idx = -1;
                float max = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

                // ToastUtil.showText(TestDetailHARFrozenAccuracyFragment.this.getActivity(), "Activity: " + labels[idx]);
                resultList.add(labels[idx]);
                // add to log
                String logStr = String.format("Attempt %s detected: %s \n", resultList.size(), labels[idx]);
                textLogs.append(logStr);
                scrollLogs.fullScroll(View.FOCUS_DOWN);

                int count = Collections.frequency(resultList, selectedLabel);
                int totalCount = resultList.size();
                float accuracy = (float)count/(float)totalCount;
                circleView_test_accuracy.setValueAnimated(accuracy*100);
                txt_test_accuracy.setText("Accuracy: " + round(accuracy,2));
                txt_test_attempts.setText("Attempts: " + resultList.size());

                ax.clear(); ay.clear(); az.clear();
                lx.clear(); ly.clear(); lz.clear();
                gx.clear(); gy.clear(); gz.clear();
                ma.clear(); ml.clear(); mg.clear();

            }else{
                timer.stop();
            }
        }
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) TestDetailHARFrozenAccuracyFragment.this.getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

}
