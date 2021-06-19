package com.qzc.mobiledlsearch.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.huxq17.download.Pump;
import com.huxq17.download.core.DownloadListener;
import com.huxq17.download.utils.LogUtil;
import com.ornach.nobobutton.NoboButton;
import com.qzc.mobiledlsearch.DownloadActivity;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.cards.SliderAdapter;

import com.qzc.mobiledlsearch.utils.OntologyAPI;
import com.qzc.mobiledlsearch.utils.ToastUtil;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Request;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getDataDirectory;
import static android.os.Environment.getDownloadCacheDirectory;


public class OverviewFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;

    private final int[] pics = {R.drawable.cnn1, R.drawable.cnn2, R.drawable.cnn3, R.drawable.cnn1, R.drawable.cnn2};

    private String[] applicationNames = SearchFragment.applicationNameList.toArray(new String[SearchFragment.applicationNameList.size()]);
    private String[] datas = SearchFragment.dataList.toArray(new String[SearchFragment.dataList.size()]);
    private String[] dataNames = SearchFragment.dataNameList.toArray(new String[SearchFragment.dataNameList.size()]);
    private String[] dataResources = SearchFragment.dataResourceList.toArray(new String[SearchFragment.dataResourceList.size()]);
    private String[] models = SearchFragment.modelList.toArray(new String[SearchFragment.modelList.size()]);
    private String[] modelNames = SearchFragment.modelNameList.toArray(new String[SearchFragment.modelNameList.size()]);
    private String[] modelResources = SearchFragment.modelResourceList.toArray(new String[SearchFragment.modelResourceList.size()]);
    private String[] accuracies = SearchFragment.accuracyList.toArray(new String[SearchFragment.accuracyList.size()]);
    private String[] precisions = SearchFragment.precisionList.toArray(new String[SearchFragment.precisionList.size()]);
    private String[] recalls = SearchFragment.recallList.toArray(new String[SearchFragment.recallList.size()]);
    private String[] f1scores = SearchFragment.f1scoreList.toArray(new String[SearchFragment.f1scoreList.size()]);
    private String[] numberOfLayers = SearchFragment.numberOfLayersList.toArray(new String[SearchFragment.numberOfLayersList.size()]);

    public static List<String> detailDataList;
    public static List<String> detailModelList;
    public static List<String> detailLayerList;

//    private final String[] domains = {"Healthcare", "Healthcare", "Healthcare", "Healthcare", "Healthcare"};
//    public static String[] applications = {"Living Activity Recognition", "Working Activity Recognition", "Health Activity Recognition", "Living Activity Recognition", "Working Activity Recognition"};
//    public static String[] datas = {"HAR Dataset 1", "HAR Dataset 2", "HAR Dataset 3", "HAR Dataset 4", "HAR Dataset 5"};
//    public static String[] models = {"CNN", "RNN", "CNN", "RNN", "CNN"};
//    private final String[] layers = {"Convolution Layer", "LSTM Layer", "Convolution Layer", "LSTM Layer", "Convolution Layer"};

    private final SliderAdapter sliderAdapter = new SliderAdapter(pics, applicationNames.length, new OnCardClickListener());

    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;

    private TextSwitcher dataSwitcher;
    private TextSwitcher modelSwitcher;

//    private TextSwitcher domainSwitcher;
    private TextSwitcher layerSwitcher;

    private ImageView btn_back;

    private TextView application1TextView;
    private TextView application2TextView;
    private int applicationOffset1;
    private int applicationOffset2;
    private long applicationAnimDuration;
    private int currentPosition;

    private CircleProgressBar progressAccuracy;
    private CircleProgressBar progressPrecision;
    private CircleProgressBar progressRecall;
    private CircleProgressBar progressF1Score;

    private NoboButton btnDetail;
    private NoboButton btnDataDownload;
    private NoboButton btnModelDownload;

    private ProgressDialog progressDialog;


    public static OverviewFragment createFor(String text) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // show location
        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = (TextView) view.findViewById(R.id.fragment_text);
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

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        application1TextView = (TextView) view.findViewById(R.id.tv_application_1);
        application2TextView = (TextView) view.findViewById(R.id.tv_application_2);

        dataSwitcher = (TextSwitcher) view.findViewById(R.id.ts_data);
        modelSwitcher = (TextSwitcher) view.findViewById(R.id.ts_model);
//        domainSwitcher = (TextSwitcher) view.findViewById(R.id.ts_domain);
        layerSwitcher = (TextSwitcher) view.findViewById(R.id.ts_layer);

        progressAccuracy = (CircleProgressBar)view.findViewById(R.id.progress_accuracy);
        progressPrecision = (CircleProgressBar)view.findViewById(R.id.progress_precision);
        progressRecall = (CircleProgressBar)view.findViewById(R.id.progress_recall);
        progressF1Score = (CircleProgressBar)view.findViewById(R.id.progress_f1score);

        // go to detail
        btnDetail = (NoboButton)view.findViewById(R.id.btn_view_detail);

        // go to data detail
        btnDataDownload = (NoboButton)view.findViewById(R.id.btn_data_download);

        // go to model detail
        btnModelDownload = (NoboButton)view.findViewById(R.id.btn_model_download);


        initProgressDialog();
        initRecyclerView();
        initApplicationText();
        initSwitchers();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(OverviewFragment.this.getActivity());
        progressDialog.setTitle("Downloading");
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    private void download(String url, String id){
        ToastUtil.showText(OverviewFragment.this.getActivity(),"Start downloading...");
//                Pump.newRequest(dataResources[0])
//                        .forceReDownload(true)
//                        .submit();
        progressDialog.setProgress(0);
        progressDialog.show();
        File filePath = OverviewFragment.this.getActivity().getDir("models1.json", Context.MODE_PRIVATE);
        String dirPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath();
        Pump.newRequest(url)
                .listener(new DownloadListener(OverviewFragment.this.getActivity()) {
                    @Override
                    public void onProgress(int progress) {
                        progressDialog.setProgress(progress);
                    }
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        // String id= Pump.getAllDownloadList().get(0).getId();
                        Toast.makeText(OverviewFragment.this.getActivity(), "Download Finished: " + getDownloadInfo().getFilePath() + ", " + getDownloadInfo().getName(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailed() {
                        progressDialog.dismiss();
                        LogUtil.e("onFailed code=" + getDownloadInfo().getErrorCode());
                        Toast.makeText(OverviewFragment.this.getActivity(), "Download Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                //Set whether to repeatedly download the downloaded file,default false.
                .forceReDownload(true)
                //Set how many threads are used when downloading,default 3.
                .threadNum(3)
                .setId(id)
                //Pump will connect server by this OKHttp request builder,so you can customize http request.
                .setRequestBuilder(new Request.Builder())
                .setRetry(3, 200)
                .submit();
    }

    private void initRecyclerView() {
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });
        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();
        new CardSnapHelper().attachToRecyclerView(recyclerView);
    }

    private void initSwitchers() {
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model_id = models[0];
                Fragment selectedScreen = DetailFragment.createFor("Detail");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, selectedScreen);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        dataSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.DataTextView, false));
        dataSwitcher.setCurrentText(dataNames[0]);

        new OverviewFragment.AsyncDataDetail().execute(datas[0]);

        btnDataDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(dataResources[0], datas[0]);
            }
        });

        modelSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.ModelTextView, false));
        modelSwitcher.setCurrentText("Model Name: " + modelNames[0]);

        new OverviewFragment.AsyncModelDetail().execute(models[0]);
        new OverviewFragment.AsyncLayerDetail().execute(models[0]);

        btnModelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(modelResources[0], models[0]);
            }
        });

        layerSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.LayerTextView, false));
        layerSwitcher.setCurrentText("Number of layers: " + numberOfLayers[0]);

        progressAccuracy.setProgress(Math.round(Float.parseFloat(accuracies[0])*100));
        progressPrecision.setProgress(Math.round(Float.parseFloat(precisions[0])*100));
        progressRecall.setProgress(Math.round(Float.parseFloat(recalls[0])*100));
        progressF1Score.setProgress(Math.round(Float.parseFloat(f1scores[0])*100));

//        domainSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.DomainTextView, true));
//        domainSwitcher.setCurrentText(domains[0]);

    }

    private void initApplicationText() {

        applicationAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        applicationOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
        applicationOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);

        application1TextView.setX(applicationOffset1);
        application2TextView.setX(applicationOffset2);
        application1TextView.setText(applicationNames[0]);
        application2TextView.setAlpha(0f);

    }

    private void setApplicationText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (application1TextView.getAlpha() > application2TextView.getAlpha()) {
            visibleText = application1TextView;
            invisibleText = application2TextView;
        } else {
            visibleText = application2TextView;
            invisibleText = application1TextView;
        }

        final int vOffset;
        if (left2right) {
            invisibleText.setX(0);
            vOffset = applicationOffset2;
        } else {
            invisibleText.setX(applicationOffset2);
            vOffset = 0;
        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", applicationOffset1);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(applicationAnimDuration);
        animSet.start();
    }

    private void onActiveCardChange() {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }
        onActiveCardChange(pos);
    }

    private void onActiveCardChange(int pos) {
        int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        setApplicationText(applicationNames[pos % applicationNames.length], left2right);

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model_id = models[pos];
                Fragment selectedScreen = DetailFragment.createFor("Detail");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, selectedScreen);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        dataSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
        dataSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
        dataSwitcher.setText(dataNames[pos % dataNames.length]);

        new OverviewFragment.AsyncDataDetail().execute(datas[pos]);

        btnDataDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(dataResources[pos], datas[pos]);
            }
        });

        modelSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
        modelSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
        modelSwitcher.setText("Model Name: " + modelNames[pos % modelNames.length]);

        new OverviewFragment.AsyncModelDetail().execute(models[pos]);
        new OverviewFragment.AsyncLayerDetail().execute(models[pos]);

        btnModelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(modelResources[pos], models[pos]);
            }
        });

        layerSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
        layerSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
        layerSwitcher.setText("Number of Layers: " + numberOfLayers[pos % numberOfLayers.length]);

        progressAccuracy.setProgress(Math.round(Float.parseFloat(accuracies[pos])*100));
        progressPrecision.setProgress(Math.round(Float.parseFloat(precisions[pos])*100));
        progressRecall.setProgress(Math.round(Float.parseFloat(recalls[pos])*100));
        progressF1Score.setProgress(Math.round(Float.parseFloat(f1scores[pos])*100));
        // ToastUtil.showText(OverviewFragment.this.getActivity(), "position: "+pos);

//        domainSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animH[0]);
//        domainSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animH[1]);
//        domainSwitcher.setText(domains[pos % domains.length]);

        currentPosition = pos;
    }

    private class TextViewFactory implements ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(OverviewFragment.this.getActivity());

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(OverviewFragment.this.getActivity(), styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }

    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            final ImageView imageView = new ImageView(OverviewFragment.this.getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final ViewGroup.LayoutParams lp = new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);

            return imageView;
        }
    }

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {
                Fragment fragment = new ImageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(com.qzc.mobiledlsearch.fragment.ImageFragment.BUNDLE_IMAGE_ID, pics[activeCardPosition % pics.length]);
                fragment.setArguments(bundle);
                final CardView cardView = (CardView) view;
                final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
                final ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(OverviewFragment.this.getActivity(), sharedView, "shared");

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();

//                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }
    }

    // async data detail information
    private class AsyncDataDetail extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getDataDetail(strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                detailDataList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Iterator iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String value = jsonObject.getJSONObject(key).getString("value");
                        detailDataList.add(key+": \r\n"+value);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // async model detail information
    private class AsyncModelDetail extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getModelDetail(strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                detailModelList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Iterator iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String value = jsonObject.getJSONObject(key).getString("value");
                        detailModelList.add(key+": \r\n"+value);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // async layer detail information
    private class AsyncLayerDetail extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getLayerDetail(strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                detailLayerList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Iterator iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String value = jsonObject.getJSONObject(key).getString("value");
                        String type = jsonObject.getJSONObject(key).getString("type");
                        if (type.equals("uri")) {
                            detailLayerList.add(key);
                        }
                        if(type.equals("literal")) {
                            detailLayerList.add(key+": "+value);
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}


