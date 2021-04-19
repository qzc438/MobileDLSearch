package com.qzc.mobiledlsearch.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
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
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.cards.SliderAdapter;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;


public class OverviewFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;

    private final int[] pics = {R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5};
    private final int[] descriptions = {R.string.text1, R.string.text2, R.string.text3, R.string.text4, R.string.text5};
    private final String[] domains = {"Healthcare", "Healthcare", "Healthcare", "Healthcare", "Healthcare"};
    private final String[] applications = {"Living Activity Recognition", "Working Activity Recognition", "Health Activity Recognition", "Living Activity Recognition", "Working Activity Recognition"};
    private final String[] places = {"The Louvre", "Gwanghwamun", "Tower Bridge", "Temple of Heaven", "Aegeana Sea"};

    private final String[] times = {"Aug 1 - Dec 15    7:00-18:00", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00"};

    private final SliderAdapter sliderAdapter = new SliderAdapter(pics, 20, new OnCardClickListener());

    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;
    private TextSwitcher domainSwitcher;
    private TextSwitcher placeSwitcher;
    private TextSwitcher clockSwitcher;
    private TextSwitcher descriptionsSwitcher;

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


    public static OverviewFragment createFor(String text) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = (TextView) view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        application1TextView = (TextView) view.findViewById(R.id.tv_application_1);
        application2TextView = (TextView) view.findViewById(R.id.tv_application_2);
        domainSwitcher = (TextSwitcher) view.findViewById(R.id.ts_domain);
        placeSwitcher = (TextSwitcher) view.findViewById(R.id.ts_place);
        clockSwitcher = (TextSwitcher) view.findViewById(R.id.ts_clock);
        descriptionsSwitcher = (TextSwitcher) view.findViewById(R.id.ts_description);
        progressAccuracy = (CircleProgressBar)view.findViewById(R.id.progress_accuracy);
        progressAccuracy.setProgress(0);
        progressPrecision = (CircleProgressBar)view.findViewById(R.id.progress_precision);
        progressPrecision.setProgress(25);
        progressRecall = (CircleProgressBar)view.findViewById(R.id.progress_recall);
        progressRecall.setProgress(50);
        progressF1Score = (CircleProgressBar)view.findViewById(R.id.progress_f1score);
        progressF1Score.setProgress(75);

        initRecyclerView();
        initCountryText();
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

        domainSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.DomainTextView, true));
        domainSwitcher.setCurrentText(domains[0]);

        placeSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.PlaceTextView, false));
        placeSwitcher.setCurrentText(places[0]);

        clockSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.ClockTextView, false));
        clockSwitcher.setCurrentText(times[0]);

        descriptionsSwitcher.setInAnimation(OverviewFragment.this.getActivity(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new OverviewFragment.TextViewFactory(R.style.DescriptionTextView, false));
        descriptionsSwitcher.setCurrentText(getString(descriptions[0]));
    }

    private void initCountryText() {
        applicationAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        applicationOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
        applicationOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);

        application1TextView.setX(applicationOffset1);
        application2TextView.setX(applicationOffset2);
        application1TextView.setText(applications[0]);
        application2TextView.setAlpha(0f);

    }

    private void setCountryText(String text, boolean left2right) {
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

        setCountryText(applications[pos % applications.length], left2right);

        domainSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animH[0]);
        domainSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animH[1]);
        domainSwitcher.setText(domains[pos % domains.length]);

        placeSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
        placeSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
        placeSwitcher.setText(places[pos % places.length]);

        clockSwitcher.setInAnimation(OverviewFragment.this.getActivity(), animV[0]);
        clockSwitcher.setOutAnimation(OverviewFragment.this.getActivity(), animV[1]);
        clockSwitcher.setText(times[pos % times.length]);

        descriptionsSwitcher.setText(getString(descriptions[pos % descriptions.length]));

        currentPosition = pos;
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

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
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }
    }

}


