package com.qzc.mobiledlsearch.fragment;

import android.os.Bundle;
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
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.utils.ToastUtil;


public class DetailFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private static final String EXTRA_DETAIL = "detail";
    private static final String DATA_ID = "data_id";
    private TextView fragmentText;
    private TextView fragmentDetail;
    private ImageView btn_back;
    private ExpandingList mExpandingList;

    private String[] detailDatas = OverviewFragment.detailDataList.toArray(new String[OverviewFragment.detailDataList.size()]);
    private String[] detailModels = OverviewFragment.detailModelList.toArray(new String[OverviewFragment.detailModelList.size()]);

    public static DetailFragment createFor(String text, String detail) {
        DetailFragment fragment = new DetailFragment();
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
        String data_id = args != null ? args.getString(DATA_ID) : "";

        btn_back = view.findViewById(R.id.btn_back);
        // back function
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        mExpandingList = view.findViewById(R.id.expanding_list_main);
        createItems();
    }

    private void createItems() {
        addItem("Data", detailDatas, R.color.yellow);
        addItem("Model", detailModels, R.color.green);
        addItem("Layer", new String[]{"Activity Type: Living Activity", "Activity Location: Single", "Activity Feature: WALKING, WALKING_UPSTAIRS, WALKING_DOWNSTAIRS, SITTING, STANDING, LAYING"}, R.color.blue);
//        addItem("Model", new String[]{"Model Name: Vanila CNN", "Model Type: CNN", "Numnber of Layers: 7"}, R.color.pink);
//        addItem("Train", new String[]{"Backend: Pytorch", "Loss Function: Categorical Crossentropy", "Optimiser: Adam"}, R.color.yellow);
//        addItem("Performance", new String[]{"Accuracy: 93%", "Precision: 83%", "Recall: 73%", "F1 Score: 63%",}, R.color.green);
//        addItem("Convolution Layer", new String[]{"Name: conv1", "Rank: 1", "Input Channel: 9", "Output Channel: 64", "Kernel Size: 3"}, R.color.pink);
//        addItem("Convolution Layer", new String[]{"Name: conv2", "Rank: 1", "Input Channel: 64", "Output Channel: 64", "Kernel Size: 3"}, R.color.yellow);
//        addItem("Dropout Layer", new String[]{"Name: dropout", "Rate: 0.5"}, R.color.green);
//        addItem("Pooling Layer", new String[]{"Name: maxpooling", "Rank: 1", "Type: maxpooling", "Kernel Size: 3"}, R.color.blue);
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
                new MaterialDialog.Builder(DetailFragment.this.getActivity())
//                        .title("title")
                        .content(tv.getText())
                        .positiveText(R.string.ok)
//                        .negativeText("no")
//                        .positiveColorRes(R.color.blue)
                        .show();
            }
        });
    }

    interface OnItemCreated {
        void itemCreated(String title);
    }
}
