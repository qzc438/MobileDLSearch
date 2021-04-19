package com.qzc.mobiledlsearch.fragment;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.guna.libmultispinner.MultiSelectionSpinner;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private ExpandingList mExpandingList;

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
        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);
        mExpandingList = view.findViewById(R.id.expanding_list_main);
        createItems();
    }

    private void createItems() {
        addItem("Application", new String[]{"Domain", "Area"}, R.color.pink);
        addItem("Data", new String[]{"Subject", "Sensor Type", "Sensor Base", "Activity Type", "Location"}, R.color.yellow);
        addItem("Model", new String[]{"Backend", "Model Type", "Loss Function", "Optimiser", "Performance"}, R.color.green);
        addItem("Layer", new String[]{"Layer Type", "Activation", "Argument"}, R.color.blue);
    }

    private void addItem(String title, String[] subItems, int colorRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            //item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            TextView tv_title = (TextView) item.findViewById(R.id.title);
            tv_title.setText(title);
            tv_title.getPaint().setFakeBoldText(true);

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
        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        MultiSelectionSpinner multiSelectionListSpinner = (MultiSelectionSpinner) view.findViewById(R.id.spinner_string_list);
        if (subTitle.equals("Domain")){
            List<String> myList = new ArrayList<>();
            myList.add("Healthcare");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        }else if (subTitle.equals("Area")){
            List<String> myList = new ArrayList<>();
            myList.add("Daily Life Monitoring");
            myList.add("Personal Biometric Signature");
            myList.add("Elderly And Youth Care");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        }else if (subTitle.equals("Subject")){
            List<String> myList = new ArrayList<>();
            myList.add("Single Subject");
            myList.add("Multiple Subject");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{1});
        }else if (subTitle.equals("Sensor Type")){
            List<String> myList = new ArrayList<>();
            myList.add("Accelerometer");
            myList.add("Gyroscope");
            myList.add("Magnetometer");
            myList.add("Pressure Sensor");
            myList.add("Temperature Sensor");
            myList.add("Humidity Sensor");
            myList.add("Camera");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0, 1});
        }else if (subTitle.equals("Sensor Base")){
            List<String> myList = new ArrayList<>();
            myList.add("Arm");
            myList.add("Belt");
            myList.add("Pocket");
            myList.add("Waist");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{2});
        }else if (subTitle.equals("Activity Type")){
            List<String> myList = new ArrayList<>();
            myList.add("Living Activity");
            myList.add("Working Activity");
            myList.add("Health Activity");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{2});
        }else if (subTitle.equals("Location")){
            List<String> myList = new ArrayList<>();
            myList.add("Single Location");
            myList.add("Multiple Location");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        }else if (subTitle.equals("Backend")){
            List<String> myList = new ArrayList<>();
            myList.add("TensorFlow");
            myList.add("Keras");
            myList.add("Pytorch");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{2});
        }else if (subTitle.equals("Model Type")){
            List<String> myList = new ArrayList<>();
            myList.add("CNN");
            myList.add("RNN");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        }else if (subTitle.equals("Loss Function")) {
            List<String> myList = new ArrayList<>();
            myList.add("Binary Crossentropy");
            myList.add("Categorical Crossentropy");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{1});
        }else if (subTitle.equals("Optimiser")) {
            List<String> myList = new ArrayList<>();
            myList.add("Adam");
            myList.add("SGD");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        }else if (subTitle.equals("Performance")) {
            List<String> myList = new ArrayList<>();
            myList.add("Loss");
            myList.add("Accruacy");
            myList.add("Precision");
            myList.add("Recall");
            myList.add("F1 Score");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{1});
        }else if (subTitle.equals("Layer Type")) {
            List<String> myList = new ArrayList<>();
            myList.add("Convolution Layer");
            myList.add("Recurrent Layer");
            myList.add("Dense Layer");
            myList.add("Reshaping Layer");
            myList.add("Pooling Layer");
            myList.add("Dropout Layer");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        } else if (subTitle.equals("Activation")) {
            List<String> myList = new ArrayList<>();
            myList.add("ReLU");
            myList.add("Softmax");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        } else if (subTitle.equals("Argument")) {
            List<String> myList = new ArrayList<>();
            myList.add("Initialiser");
            myList.add("Constraint");
            myList.add("Regulariser");
            multiSelectionListSpinner.setTitle(subTitle);
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{1});
        } else {
            List<String> myList = new ArrayList<>();
            myList.add("No options available");
            multiSelectionListSpinner.setTitle("Unknown");
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0});
        }

        multiSelectionListSpinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices, MultiSelectionSpinner spinner) {
            }
            @Override
            public void selectedStrings(List<String> strings, MultiSelectionSpinner spinner) {
                ToastUtil.showText(SearchFragment.this.getActivity(), "List : " + strings.toString()+", Title: " + spinner.getTitle());
            }
        });

    }

    interface OnItemCreated {
        void itemCreated(String title);
    }
}
