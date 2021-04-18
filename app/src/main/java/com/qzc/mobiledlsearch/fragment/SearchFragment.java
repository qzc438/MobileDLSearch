package com.qzc.mobiledlsearch.fragment;

import android.os.Bundle;
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
import java.util.List;


public class SearchFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
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
//        Bundle args = getArguments();
//        final String text = args != null ? args.getString(EXTRA_TEXT) : "";
//        TextView textView = view.findViewById(R.id.text);
//        textView.setText(text);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), text, Toast.LENGTH_SHORT).show();
//            }
//        });
        mExpandingList = view.findViewById(R.id.expanding_list_main);
        createItems();
    }

    private void createItems() {
        addItem("Application", new String[]{"Domain", "Area"}, R.color.pink, R.drawable.ic_ghost);
        addItem("Data", new String[]{"Subject", "Sensor", "Activity"}, R.color.yellow, R.drawable.ic_ghost);
        addItem("Model", new String[]{"Model Type", "Loss Function", "Optimiser", "Performance"}, R.color.green, R.drawable.ic_ghost);
        addItem("Layer", new String[]{"Core Layer", "Functional layer"}, R.color.blue, R.drawable.ic_ghost);
    }

    private void addItem(String title, String[] subItems, int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);

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
            myList.add("One");
            myList.add("Two");
            myList.add("Three");
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{0, 2});
        }else{
            List<String> myList = new ArrayList<>();
            myList.add("1");
            myList.add("2");
            myList.add("3");
            multiSelectionListSpinner.setItems(myList);
            multiSelectionListSpinner.setSelection(new int[]{1, 2});
        }
        multiSelectionListSpinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices, MultiSelectionSpinner spinner) {
            }
            @Override
            public void selectedStrings(List<String> strings, MultiSelectionSpinner spinner) {
                switch (spinner.getId()) {
                    case R.id.spinner_string_array:
                        ToastUtil.showText(SearchFragment.this.getActivity(), "Array : " + strings.toString());
                        break;
                    case R.id.spinner_string_list:
                        ToastUtil.showText(SearchFragment.this.getActivity(), "List : " + strings.toString());
                        break;
                }
            }
        });

    }

    interface OnItemCreated {
        void itemCreated(String title);
    }
}
