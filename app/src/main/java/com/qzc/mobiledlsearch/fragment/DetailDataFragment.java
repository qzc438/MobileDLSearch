package com.qzc.mobiledlsearch.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.guna.libmultispinner.MultiSelectionSpinner;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.util.ToastUtil;
import com.qzc.mobiledlsearch.utils.TextJustification;

import java.util.ArrayList;
import java.util.List;


public class DetailDataFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private static final String EXTRA_DETAIL = "detail";
    private TextView fragmentText;
    private TextView fragmentDetail;
    private ExpandingList mExpandingList;

    public static DetailDataFragment createFor(String text, String detail) {
        DetailDataFragment fragment = new DetailDataFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        args.putString(EXTRA_DETAIL, detail);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);
        String detail = args != null ? args.getString(EXTRA_DETAIL) : "";
        fragmentDetail = view.findViewById(R.id.fragment_detail);
        fragmentDetail.setText(detail);
        mExpandingList = view.findViewById(R.id.expanding_list_main);
        createItems();
    }

    private void createItems() {
        addItem("Dataset", new String[]{"Dataset Name: UCI Smartphones Data Set", "Dataset Description: The experiments have been carried out with a group of 30 volunteers within an age bracket of 19-48 years. Each person performed six activities (WALKING, WALKING_UPSTAIRS, WALKING_DOWNSTAIRS, SITTING, STANDING, LAYING) wearing a smartphone (Samsung Galaxy S II) on the waist. Using its embedded accelerometer and gyroscope, we captured 3-axial linear acceleration and 3-axial angular velocity at a constant rate of 50Hz. The experiments have been video-recorded to label the data manually. The obtained dataset has been randomly partitioned into two sets, where 70% of the volunteers was selected for generating the training data and 30% the test data.The sensor signals (accelerometer and gyroscope) were pre-processed by applying noise filters and then sampled in fixed-width sliding windows of 2.56 sec and 50% overlap (128 readings/window). The sensor acceleration signal, which has gravitational and body motion components, was separated using a Butterworth low-pass filter into body acceleration and gravity. The gravitational force is assumed to have only low frequency components, therefore a filter with 0.3 Hz cutoff frequency was used. From each window, a vector of features was obtained by calculating variables from the time and frequency domain."}, R.color.pink);
        addItem("Subject", new String[]{"Subject Type: Single"}, R.color.yellow);
        addItem("Sensor", new String[]{"Sensor Type: Accelerometer, Gyroscope", "Sensor Base: Pocket"}, R.color.green);
        addItem("Activity", new String[]{"Activity Type: Living Activity", "Activity Location: Single", "Activity Feature: WALKING, WALKING_UPSTAIRS, WALKING_DOWNSTAIRS, SITTING, STANDING, LAYING"}, R.color.blue);
    }

    private void addItem(String title, String[] subItems, int colorRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout_detail);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            //item.setIndicatorIconRes(iconRes);

            //It is possible to get any view inside the inflated layout. Let's set the text in the item
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
                configureSubItem(item, view, subItems[i]);
            }
        }
    }

    private void configureSubItem(final ExpandingItem item, final View view, String subTitle) {
        TextView tv = (TextView) view.findViewById(R.id.sub_title);
        tv.setText(subTitle);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set dialog here
                new MaterialDialog.Builder(DetailDataFragment.this.getActivity())
//                        .title("title")
                        .content(tv.getText())
                        .positiveText(R.string.ok)
//                        .negativeText("no")
//                        .positiveColorRes(R.color.blue)
                        .show();
            }
        });
//        TextJustification.justify(tv, 80);
    }

    interface OnItemCreated {
        void itemCreated(String title);
    }
}
