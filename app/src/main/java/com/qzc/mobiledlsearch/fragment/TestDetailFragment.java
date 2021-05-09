package com.qzc.mobiledlsearch.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qzc.mobiledlsearch.HARClassifier;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.entity.ActivityBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TestDetailFragment extends Fragment implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;

    // recycle view
    private final HashMap<ActicityViewHolder, ActivityBean> map = new HashMap<>();
    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private List<ActivityBean> ActivityList;

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

    private float[] results;
    private HARClassifier classifier;

    private TextToSpeech textToSpeech;

    private String[] labels = {"Biking", "Downstairs", "Jogging", "Sitting", "Standing", "Upstairs", "Walking"};


    public static TestDetailFragment createFor(String text) {
        TestDetailFragment fragment = new TestDetailFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);

        recyclerView = view.findViewById(R.id.rvActivityList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TestDetailFragment.this.getActivity());

        ActivityList = new ArrayList<ActivityBean>();
        //Get all feature list
        for(String label: labels){
            ActivityBean activityBean = new ActivityBean(label, 0.00f);
            ActivityList.add(activityBean);
        }
        recyclerView.setLayoutManager(linearLayoutManager);
        activityAdapter = new ActivityAdapter(map, ActivityList);
        recyclerView.setAdapter(activityAdapter);

        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();
        ma = new ArrayList<>(); ml = new ArrayList<>(); mg = new ArrayList<>();

        mSensorManager = (SensorManager) TestDetailFragment.this.getActivity().getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mLinearAcceleration , SensorManager.SENSOR_DELAY_FASTEST);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_FASTEST);

        classifier = new HARClassifier(TestDetailFragment.this.getActivity().getApplicationContext());

        textToSpeech = new TextToSpeech(TestDetailFragment.this.getActivity(), this);
        textToSpeech.setLanguage(Locale.US);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

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

            setProbabilities();

            ax.clear(); ay.clear(); az.clear();
            lx.clear(); ly.clear(); lz.clear();
            gx.clear(); gy.clear(); gz.clear();
            ma.clear(); ml.clear(); mg.clear();
        }
    }

    private void setProbabilities() {
        for (ActivityBean activityBean:ActivityList)
        {
            if(activityBean.getActivityName().equals("Biking")){
                activityBean.setActivityProbability(round(results[0], 2));
            }
            if(activityBean.getActivityName().equals("Downstairs")){
                activityBean.setActivityProbability(round(results[1], 2));
            }
            if(activityBean.getActivityName().equals("Jogging")){
                activityBean.setActivityProbability(round(results[2], 2));
            }
            if(activityBean.getActivityName().equals("Sitting")){
                activityBean.setActivityProbability(round(results[3], 2));
            }
            if(activityBean.getActivityName().equals("Standing")){
                activityBean.setActivityProbability(round(results[4], 2));
            }
            if(activityBean.getActivityName().equals("Upstairs")){
                activityBean.setActivityProbability(round(results[5], 2));
            }
            if(activityBean.getActivityName().equals("Walking")){
                activityBean.setActivityProbability(round(results[6], 2));
            }
            // inform the List has changed
            activityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
            }
        }, 1000, 2000);
    }

    public class ActivityAdapter extends RecyclerView.Adapter<ActicityViewHolder> {
        List<ActivityBean> activityBeanList;
        HashMap<ActicityViewHolder, ActivityBean> map;

        public ActivityAdapter(HashMap<ActicityViewHolder, ActivityBean> map, List<ActivityBean> activityBeanList) {
            this.activityBeanList = activityBeanList;
            this.map = map;
        }

        @NonNull
        @Override
        public ActicityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_test_list, viewGroup, false);
            return new ActicityViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder(@NonNull ActicityViewHolder viewHolder, int position) {
            ActivityBean activityBean = activityBeanList.get(position);
            map.put(viewHolder, activityBean);
            viewHolder.bindData(activityBean);
            if(idx == position){
                viewHolder.tv_probability.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBlue, null));
            }else{
                viewHolder.tv_probability.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
            }
        }

        public void delete(ActicityViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
            activityBeanList.remove(position);
            notifyItemRemoved(position);
            map.remove(viewHolder);
        }

        @Override
        public int getItemCount() {
            return activityBeanList.size();
        }
    }

    public class ActicityViewHolder extends RecyclerView.ViewHolder{

        TextView tv_activity;
        TextView tv_probability;

        public ActicityViewHolder(@NonNull View itemView, final ActivityAdapter adapter) {
            super(itemView);
            tv_activity = itemView.findViewById(R.id.tv_activity);
            tv_probability = itemView.findViewById(R.id.tv_probability);
        }

        public void bindData(ActivityBean activityBean) {
            tv_activity.setText(activityBean.getActivityName());
            tv_probability.setText(activityBean.getActivityProbability()+"");
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
        return (SensorManager) TestDetailFragment.this.getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

}
