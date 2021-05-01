package com.qzc.mobiledlsearch.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.gson.Gson;
import com.ornach.nobobutton.NoboButton;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.cards.SliderAdapter;

import com.qzc.mobiledlsearch.entity.ParameterBean;
import com.qzc.mobiledlsearch.utils.OntologyAPI;
import com.qzc.mobiledlsearch.utils.ToastUtil;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OverviewFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;

    private final int[] pics = {R.drawable.lstm, R.drawable.lstm_stacked, R.drawable.lstm_attantion, R.drawable.lstm, R.drawable.lstm_stacked};

    public static String[] applicationNames = SearchFragment.applicationNameList.toArray(new String[SearchFragment.applicationNameList.size()]);;
    public static String[] datas = SearchFragment.dataList.toArray(new String[SearchFragment.dataList.size()]);
    public static String[] dataNames = SearchFragment.dataNameList.toArray(new String[SearchFragment.dataNameList.size()]);
    public static String[] models = SearchFragment.modelList.toArray(new String[SearchFragment.modelList.size()]);
    public static String[] modelNames = SearchFragment.modelNameList.toArray(new String[SearchFragment.modelNameList.size()]);
    public static String[] accuracies = SearchFragment.accuracyList.toArray(new String[SearchFragment.accuracyList.size()]);
    public static String[] precisions = SearchFragment.precisionList.toArray(new String[SearchFragment.precisionList.size()]);
    public static String[] recalls = SearchFragment.recallList.toArray(new String[SearchFragment.recallList.size()]);
    public static String[] f1scores = SearchFragment.f1scoreList.toArray(new String[SearchFragment.f1scoreList.size()]);

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
//    private TextSwitcher layerSwitcher;

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

    private NoboButton btnDataDetail;
    private NoboButton btnModelDetail;
    private NoboButton btnLayerDetail;


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
//        layerSwitcher = (TextSwitcher) view.findViewById(R.id.ts_layer);

        progressAccuracy = (CircleProgressBar)view.findViewById(R.id.progress_accuracy);
        progressPrecision = (CircleProgressBar)view.findViewById(R.id.progress_precision);
        progressRecall = (CircleProgressBar)view.findViewById(R.id.progress_recall);
        progressF1Score = (CircleProgressBar)view.findViewById(R.id.progress_f1score);

        // go to data detail
        btnDataDetail = (NoboButton)view.findViewById(R.id.btn_view_data_detail);
        btnDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedScreen = DetailDataFragment.createFor("Detail", "Data");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, selectedScreen);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
//                OverviewFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedScreen).commit();
            }
        });

        // go to model detail
        btnModelDetail = (NoboButton)view.findViewById(R.id.btn_view_model_detail);
        btnModelDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedScreen = DetailModelFragment.createFor("Detail", "Model");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, selectedScreen);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
//                OverviewFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedScreen).commit();
            }
        });

        // go to model detail
        btnLayerDetail = (NoboButton)view.findViewById(R.id.btn_view_layer_detail);
        btnLayerDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedScreen = DetailLayerFragment.createFor("Detail", "Layer");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, selectedScreen);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
//                OverviewFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedScreen).commit();
            }
        });

        initRecyclerView();
        initApplicationText();
        initSwitchers();
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

        dataSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.DataTextView, false));
        dataSwitcher.setCurrentText(dataNames[0]);

        modelSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.ModelTextView, false));
        modelSwitcher.setCurrentText(modelNames[0]);

        progressAccuracy.setProgress(Math.round(Float.parseFloat(accuracies[0])*100));
        progressPrecision.setProgress(Math.round(Float.parseFloat(precisions[0])*100));
        progressRecall.setProgress(Math.round(Float.parseFloat(recalls[0])*100));
        progressF1Score.setProgress(Math.round(Float.parseFloat(f1scores[0])*100));

//        domainSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.DomainTextView, true));
//        domainSwitcher.setCurrentText(domains[0]);

//        layerSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.LayerTextView, false));
//        layerSwitcher.setCurrentText(layers[0]);

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

        dataSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
        dataSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
        dataSwitcher.setText(dataNames[pos % dataNames.length]);

        progressAccuracy.setProgress(Math.round(Float.parseFloat(accuracies[pos])*100));
        progressPrecision.setProgress(Math.round(Float.parseFloat(precisions[pos])*100));
        progressRecall.setProgress(Math.round(Float.parseFloat(recalls[pos])*100));
        progressF1Score.setProgress(Math.round(Float.parseFloat(f1scores[pos])*100));
        ToastUtil.showText(OverviewFragment.this.getActivity(), "position: "+pos);

        modelSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
        modelSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
        modelSwitcher.setText(modelNames[pos % modelNames.length]);

//        domainSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animH[0]);
//        domainSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animH[1]);
//        domainSwitcher.setText(domains[pos % domains.length]);

//        layerSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
//        layerSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
//        layerSwitcher.setText(layers[pos % layers.length]);

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

}


