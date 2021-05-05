package com.qzc.mobiledlsearch.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.google.gson.Gson;
import com.guna.libmultispinner.MultiSelectionSpinner;
import com.ornach.nobobutton.NoboButton;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.entity.ParameterBean;
import com.qzc.mobiledlsearch.utils.OntologyAPI;
import com.qzc.mobiledlsearch.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;


public class SearchFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private NoboButton btnSearch;
    private ExpandingList mExpandingList;

    public static ParameterBean parameterBean;

    public static List<String> applicationNameList;
    public static List<String> modelList;
    public static List<String> modelNameList;
    public static List<String> modelResourceList;
    public static List<String> dataList;
    public static List<String> dataNameList;
    public static List<String> dataResourceList;
    public static List<String> accuracyList;
    public static List<String> precisionList;
    public static List<String> recallList;
    public static List<String> f1scoreList;
    public static List<String> numberOfLayersList;

    private CircleProgressView circleView_accuracy;
    private SeekBar seekBar_accuracy;
    private CircleProgressView circleView_precision;
    private SeekBar seekBar_precision;
    private CircleProgressView circleView_recall;
    private SeekBar seekBar_recall;
    private CircleProgressView circleView_f1score;
    private SeekBar seekBar_f1score;

    private MultiSelectionSpinner multiSelectionListSpinner;

    public static SearchFragment createFor(String text) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //show location
        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);

        // search function
        btnSearch = view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(applicationNameList.size()==0){
                    ToastUtil.showText(SearchFragment.this.getActivity(), "No results available.\r\nPlease change your filters.");
                } else{
                    Fragment selectedScreen = OverviewFragment.createFor("Overview");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.container, selectedScreen);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        // performance metrics
        circleView_accuracy = (CircleProgressView) view.findViewById(R.id.circleView_accuracy);
        circleView_accuracy.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                // ToastUtil.showText(SearchFragment.this.getActivity(), "value:"+value);
            }
        });
        seekBar_accuracy = (SeekBar) view.findViewById(R.id.seekBar_accuracy);
        seekBar_accuracy.setMax(100);
        seekBar_accuracy.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // search result
                        if (parameterBean==null){
                            parameterBean = new ParameterBean();
                        }
                        parameterBean.setAccuracy((double)seekBar.getProgress()/100+"");
                        Gson gson = new Gson();
                        String jsonParameterBean = gson.toJson(parameterBean);
                        new SearchFragment.AsyncOverviewInformation().execute(jsonParameterBean);
                        // set progress bar
                        circleView_accuracy.setValueAnimated(seekBar.getProgress(), 1500);
                    }
                }
        );

        circleView_precision = (CircleProgressView) view.findViewById(R.id.circleView_precision);
        circleView_precision.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                // ToastUtil.showText(SearchFragment.this.getActivity(), "value:"+value);
            }
        });
        seekBar_precision = (SeekBar) view.findViewById(R.id.seekBar_precision);
        seekBar_precision.setMax(100);
        seekBar_precision.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // search result
                        if (parameterBean==null){
                            parameterBean = new ParameterBean();
                        }
                        parameterBean.setPrecision((double)seekBar.getProgress()/100+"");
                        Gson gson = new Gson();
                        String jsonParameterBean = gson.toJson(parameterBean);
                        new SearchFragment.AsyncOverviewInformation().execute(jsonParameterBean);
                        // set progress bar
                        circleView_precision.setValueAnimated(seekBar.getProgress(), 1500);
                    }
                }
        );

        circleView_recall = (CircleProgressView) view.findViewById(R.id.circleView_recall);
        circleView_recall.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                // ToastUtil.showText(SearchFragment.this.getActivity(), "value:"+value);
            }
        });
        seekBar_recall = (SeekBar) view.findViewById(R.id.seekBar_recall);
        seekBar_recall.setMax(100);
        seekBar_recall.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // search result
                        if (parameterBean==null){
                            parameterBean = new ParameterBean();
                        }
                        parameterBean.setRecall((double)seekBar.getProgress()/100+"");
                        Gson gson = new Gson();
                        String jsonParameterBean = gson.toJson(parameterBean);
                        new SearchFragment.AsyncOverviewInformation().execute(jsonParameterBean);
                        // set progress bar
                        circleView_recall.setValueAnimated(seekBar.getProgress(), 1500);
                    }
                }
        );

        circleView_f1score = (CircleProgressView) view.findViewById(R.id.circleView_f1score);
        circleView_f1score.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                // ToastUtil.showText(SearchFragment.this.getActivity(), "value:"+value);
            }
        });
        seekBar_f1score = (SeekBar) view.findViewById(R.id.seekBar_f1score);
        seekBar_f1score.setMax(100);
        seekBar_f1score.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // search result
                        if (parameterBean==null){
                            parameterBean = new ParameterBean();
                        }
                        parameterBean.setF1score((double)seekBar.getProgress()/100+"");
                        Gson gson = new Gson();
                        String jsonParameterBean = gson.toJson(parameterBean);
                        new SearchFragment.AsyncOverviewInformation().execute(jsonParameterBean);
                        // set progress bar
                        circleView_f1score.setValueAnimated(seekBar.getProgress(), 1500);
                    }
                }
        );

        // show title and sub-title
        mExpandingList = view.findViewById(R.id.expanding_list_main);
        createItems();
    }

    private void createItems() {
        addItem("Application", new String[]{"Domain", "Area"}, R.color.pink);
        addItem("Data", new String[]{"Subject", "Sensor Type", "Sensor Base", "Activity Type", "Location"}, R.color.yellow);
        addItem("Model", new String[]{"Backend", "Model Type", "Loss Function", "Optimiser", "Performance"}, R.color.green);
        addItem("Layer", new String[]{"Layer Type", "Core", "Functional","Activation", "Argument"}, R.color.blue);
    }

    private void addItem(String title, String[] subItems, int colorRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);
        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            //item.setIndicatorIconRes(iconRes);

            TextView tv_title = (TextView) item.findViewById(R.id.title);
            tv_title.setText(title);
            tv_title.getPaint().setFakeBoldText(true);

            item.setStateChangedListener(new ExpandingItem.OnItemStateChanged() {
                @Override
                public void itemCollapseStateChanged(boolean expanded) {
                    if(item.isExpanded()){
                        ImageView imageView = (ImageView)item.findViewById(R.id.img_expand);
                        imageView.setImageResource(R.drawable.outline_remove_circle_outline_black_36);
                    } else{
                        ImageView imageView = (ImageView)item.findViewById(R.id.img_expand);
                        imageView.setImageResource(R.drawable.outline_add_circle_outline_black_36);
                    }
                }
            });

            //We can create items in batch.
            item.createSubItems(subItems.length);
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);
                //Let's set some values in
                configureSubItem(title, view, subItems[i]);
            }
        }
    }

    private void configureSubItem(String title, final View view, String subTitle) {
        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        multiSelectionListSpinner = (MultiSelectionSpinner) view.findViewById(R.id.spinner_string_list);
                if (subTitle.equals("Domain")){
                    List<String> myList = new ArrayList<>();
                    myList.addAll(HomeFragment.applicationDomainList);
                    multiSelectionListSpinner.setItems(myList);
                    // multiSelectionListSpinner.setSelection(new int[]{0, 1});
                    // myList.add("Healthcare");
                }else if (subTitle.equals("Area")){
                    List<String> myList = new ArrayList<>();
                    if (HomeFragment.applicationAreaList !=null){
                        myList.addAll(HomeFragment.applicationAreaList);
                        multiSelectionListSpinner.setItems(myList);
                    }
                    // myList.add("Daily Life Monitoring");
                    // myList.add("Personal Biometric Signature");
                    // myList.add("Elderly And Youth Care");
                }else if (subTitle.equals("Subject")){
                    List<String> myList = new ArrayList<>();
                    myList.add("Single Subject");
                    myList.add("Multiple Subject");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Sensor Type")){
                    List<String> myList = new ArrayList<>();
                    myList.addAll(HomeFragment.dataSensorTypeList);
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                    // myList.add("Accelerometer");
                    // myList.add("Gyroscope");
                    // myList.add("Magnetometer");
                    // myList.add("Pressure Sensor");
                    // myList.add("Temperature Sensor");
                    // myList.add("Humidity Sensor");
                    // myList.add("Camera");
                }else if (subTitle.equals("Sensor Base")){
                    List<String> myList = new ArrayList<>();
                    myList.add("Arm");
                    myList.add("Belt");
                    myList.add("Pocket");
                    myList.add("Waist");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Activity Type")){
                    List<String> myList = new ArrayList<>();
                    myList.add("Living Activity");
                    myList.add("Working Activity");
                    myList.add("Health Activity");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Location")){
                    List<String> myList = new ArrayList<>();
                    myList.add("Single Location");
                    myList.add("Multiple Location");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Backend")){
                    List<String> myList = new ArrayList<>();
                    myList.add("TensorFlow");
                    myList.add("Keras");
                    myList.add("Pytorch");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Model Type")){
                    List<String> myList = new ArrayList<>();
                    myList.addAll(HomeFragment.modelTypeList);
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                    // myList.add("CNN");
                    // myList.add("RNN");
                }else if (subTitle.equals("Loss Function")) {
                    List<String> myList = new ArrayList<>();
                    myList.add("Binary Crossentropy");
                    myList.add("Categorical Crossentropy");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Optimiser")) {
                    List<String> myList = new ArrayList<>();
                    myList.add("Adam");
                    myList.add("SGD");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Performance")) {
                    List<String> myList = new ArrayList<>();
                    myList.add("Loss");
                    myList.add("Accruacy");
                    myList.add("Precision");
                    myList.add("Recall");
                    myList.add("F1 Score");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                }else if (subTitle.equals("Layer Type")) {
                    List<String> myList = new ArrayList<>();
                    myList.addAll(HomeFragment.layerTypeList);
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                    // myList.add("Core Layer");
                    // myList.add("Functional Layer");
                } else if (subTitle.equals("Core")) {
                    List<String> myList = new ArrayList<>();
                    myList.addAll(HomeFragment.coreLayerTypeList);
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                    // myList.add("Convolution Layer");
                    // myList.add("Recurrent Layer");
                    // myList.add("Dense Layer");
                } else if (subTitle.equals("Functional")) {
                    List<String> myList = new ArrayList<>();
                    myList.addAll(HomeFragment.functionalLayerTypeList);
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                    // myList.add("Reshaping Layer");
                    // myList.add("Pooling Layer");
                    // myList.add("Dropout Layer");
                } else if (subTitle.equals("Activation")) {
                    List<String> myList = new ArrayList<>();
                    myList.add("ReLU");
                    myList.add("Softmax");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                } else if (subTitle.equals("Argument")) {
                    List<String> myList = new ArrayList<>();
                    myList.add("Initialiser");
                    myList.add("Constraint");
                    myList.add("Regulariser");
                    multiSelectionListSpinner.setTitle(subTitle);
                    multiSelectionListSpinner.setItems(myList);
                } else {
                    List<String> myList = new ArrayList<>();
                    myList.add("No options available");
                    multiSelectionListSpinner.setTitle("Unknown");
                    multiSelectionListSpinner.setItems(myList);
                }

        // set listener to invoke search when filter changes
        multiSelectionListSpinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {

            @Override
            public void selectedIndices(List<Integer> indices, MultiSelectionSpinner spinner) {
            }

            @Override
            public void selectedStrings(List<String> strings, MultiSelectionSpinner spinner) {
//                SharedPreferences mySharedPreferences= SearchFragment.this.getActivity().getSharedPreferences("filter", SearchFragment.this.getActivity().MODE_PRIVATE);
//                SharedPreferences.Editor editor = mySharedPreferences.edit();
//                editor.putString(spinner.getTitle(), strings.toString());
//                editor.commit();
                if (parameterBean==null){
                    parameterBean = new ParameterBean();
                }
                if(title.equals("Application") && subTitle.equals("Domain")){
                    parameterBean.setApplicationDomain(strings);
                }
                if(title.equals("Application") && subTitle.equals("Area")){
                    parameterBean.setApplicationArea(strings);
                }
                if(title.equals("Data") && subTitle.equals("Sensor Type")){
                    parameterBean.setDataSourceType(strings);
                }
                if(title.equals("Model") && subTitle.equals("Model Type")){
                    parameterBean.setModelType(strings);
                }

                Gson gson = new Gson();
                String jsonParameterBean = gson.toJson(parameterBean);
                new SearchFragment.AsyncOverviewInformation().execute(jsonParameterBean);

                // ToastUtil.showText(SearchFragment.this.getActivity(), parameterBean.getApplicationArea().toString());
            }
        });
    }

    // async overview information
    private class AsyncOverviewInformation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getOverviewInformation(strings[0]);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            try {
                applicationNameList = new ArrayList<String>();
                modelList = new ArrayList<String>();
                modelNameList = new ArrayList<String>();
                modelResourceList = new ArrayList<String>();
                dataList = new ArrayList<String>();
                dataNameList = new ArrayList<String>();
                dataResourceList = new ArrayList<String>();
                accuracyList = new ArrayList<String>();
                precisionList = new ArrayList<String>();
                recallList = new ArrayList<String>();
                f1scoreList = new ArrayList<String>();
                numberOfLayersList = new ArrayList<String>();

                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("applicationName")) {
                        String a = jsonObject.getJSONObject("applicationName").getString("value");
                        applicationNameList.add(a);
                    }
                    if (jsonObject.has("data")) {
                        String a = jsonObject.getJSONObject("data").getString("value").split("#")[1];
                        dataList.add(a);
                    }
                    if (jsonObject.has("dataName")) {
                        String a = jsonObject.getJSONObject("dataName").getString("value");
                        dataNameList.add(a);
                    }
                    if (jsonObject.has("dataResource")) {
                        String a = jsonObject.getJSONObject("dataResource").getString("value");
                        dataResourceList.add(a);
                    }
                    if (jsonObject.has("model")) {
                        String a = jsonObject.getJSONObject("model").getString("value").split("#")[1];
                        modelList.add(a);
                    }
                    if (jsonObject.has("modelName")) {
                        String a = jsonObject.getJSONObject("modelName").getString("value");
                        modelNameList.add(a);
                    }
                    if (jsonObject.has("modelResource")) {
                        String a = jsonObject.getJSONObject("modelResource").getString("value");
                        modelResourceList.add(a);
                    }
                    if (jsonObject.has("performanceAccuracy")) {
                        String a = jsonObject.getJSONObject("performanceAccuracy").getString("value");
                        accuracyList.add(a);
                    }
                    if (jsonObject.has("performancePrecision")) {
                        String a = jsonObject.getJSONObject("performancePrecision").getString("value");
                        precisionList.add(a);
                    }
                    if (jsonObject.has("performanceRecall")) {
                        String a = jsonObject.getJSONObject("performanceRecall").getString("value");
                        recallList.add(a);
                    }
                    if (jsonObject.has("performanceF1Score")) {
                        String a = jsonObject.getJSONObject("performanceF1Score").getString("value");
                        f1scoreList.add(a);
                    }
                    if (jsonObject.has("numberOfLayers")) {
                        String a = jsonObject.getJSONObject("numberOfLayers").getString("value");
                        numberOfLayersList.add(a);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    interface OnItemCreated {
        void itemCreated(String title);
    }
}
