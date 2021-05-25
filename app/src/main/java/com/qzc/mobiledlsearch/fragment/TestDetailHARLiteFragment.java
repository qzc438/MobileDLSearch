package com.qzc.mobiledlsearch.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qzc.mobiledlsearch.Classifier;
import com.qzc.mobiledlsearch.HARFrozenClassifier;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.TensorFlowHARClassifier;
import com.qzc.mobiledlsearch.entity.ActivityBean;
import com.wonderkiln.camerakit.CameraView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestDetailHARLiteFragment extends Fragment implements SensorEventListener, TextToSpeech.OnInitListener{

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private ImageView btn_back;

    private static final String MODEL_PATH = "CNN_Keras.tflite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels2.txt";
    private static final int N_SAMPLES = 128;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();

    private static List<Float> ax;
    private static List<Float> ay;
    private static List<Float> az;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;

    // recycle view
    private final HashMap<ActicityViewHolder, ActivityBean> map = new HashMap<>();
    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private List<ActivityBean> ActivityList;

    int idx = -1;
    private float[][] results;
    private String[] labels = {"WALKING", "WALKING_UPSTAIRS", "WALKING_DOWNSTAIRS", "SITTING", "STANDING", "LAYING"};

    private TextToSpeech textToSpeech;

    public static TestDetailHARLiteFragment createFor(String text) {
        TestDetailHARLiteFragment fragment = new TestDetailHARLiteFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_detail_har, container, false);
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

        recyclerView = view.findViewById(R.id.rvActivityList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TestDetailHARLiteFragment.this.getActivity());

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

        mSensorManager = (SensorManager) TestDetailHARLiteFragment.this.getActivity().getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mLinearAcceleration , SensorManager.SENSOR_DELAY_FASTEST);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_FASTEST);

        textToSpeech = new TextToSpeech(TestDetailHARLiteFragment.this.getActivity(), this);
        textToSpeech.setLanguage(Locale.US);

        initTensorFlowAndLoadModel();
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowHARClassifier.create(
                            TestDetailHARLiteFragment.this.getActivity().getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            N_SAMPLES,
                            QUANT);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
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

        if (ax.size() >= N_SAMPLES && ay.size() >= N_SAMPLES && az.size() >= N_SAMPLES
                && lx.size() >= N_SAMPLES && ly.size() >= N_SAMPLES && lz.size() >= N_SAMPLES
                && gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES
        ) {
            float[][][] data = new float[1][128][9];
            for (int i = 0; i<128; i++){

                data[0][i][0]  = lx.get(i);
                data[0][i][1]  = ly.get(i);
                data[0][i][2]  = lz.get(i);
                data[0][i][3]  = gx.get(i);
                data[0][i][4]  = gy.get(i);
                data[0][i][5]  = gz.get(i);
                data[0][i][6]  = ax.get(i);
                data[0][i][7]  = ay.get(i);
                data[0][i][8]  = az.get(i);
            }

            Log.e("data...", data[0][0][0]+"");
            results = classifier.recognizeHAR(data);
            Log.e("Sensor...", results[0][0]+"");

            // find the max position
            idx = -1;
            float max = -1;
            for (int i = 0; i < results[0].length; i++) {
                if (results[0][i] > max) {
                    idx = i;
                    max = results[0][i];
                }
            }

            setProbabilities();

            ax.clear(); ay.clear(); az.clear();
            lx.clear(); ly.clear(); lz.clear();
            gx.clear(); gy.clear(); gz.clear();
        }
    }

    private void setProbabilities() {
        for (ActivityBean activityBean:ActivityList)
        {
            if(activityBean.getActivityName().equals("WALKING")){
                activityBean.setActivityProbability(round(results[0][0], 2));
            }
            if(activityBean.getActivityName().equals("WALKING_UPSTAIRS")){
                activityBean.setActivityProbability(round(results[0][1], 2));
            }
            if(activityBean.getActivityName().equals("WALKING_DOWNSTAIRS")){
                activityBean.setActivityProbability(round(results[0][2], 2));
            }
            if(activityBean.getActivityName().equals("SITTING")){
                activityBean.setActivityProbability(round(results[0][3], 2));
            }
            if(activityBean.getActivityName().equals("STANDING")){
                activityBean.setActivityProbability(round(results[0][4], 2));
            }
            if(activityBean.getActivityName().equals("LAYING")){
                activityBean.setActivityProbability(round(results[0][5], 2));
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
        }, 2000, 2000);
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
                viewHolder.layout_activity_list.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBlue, null));
            }else{
                viewHolder.layout_activity_list.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
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
        LinearLayout layout_activity_list;

        public ActicityViewHolder(@NonNull View itemView, final ActivityAdapter adapter) {
            super(itemView);
            tv_activity = itemView.findViewById(R.id.tv_activity);
            tv_probability = itemView.findViewById(R.id.tv_probability);
            layout_activity_list = itemView.findViewById(R.id.layout_activity_list);
        }

        public void bindData(ActivityBean activityBean) {
            tv_activity.setText(activityBean.getActivityName());
            tv_probability.setText(activityBean.getActivityProbability()+"");
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) TestDetailHARLiteFragment.this.getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

}
