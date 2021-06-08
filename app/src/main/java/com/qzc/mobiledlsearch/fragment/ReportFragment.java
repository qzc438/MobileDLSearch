package com.qzc.mobiledlsearch.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.qzc.mobiledlsearch.R;

public class ReportFragment extends Fragment{

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private ImageView btn_back;

    public static ReportFragment createFor(String text) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private LineChart chart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_report, container, false);

        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = v.findViewById(R.id.fragment_text);
        fragmentText.setText(text);

        // show backwards
        btn_back = v.findViewById(R.id.btn_back);
        // back function
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        ViewPager pager = v.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);

        PageAdapter a = new PageAdapter(getChildFragmentManager());
        pager.setAdapter(a);

        return v;
    }

    private class PageAdapter extends FragmentPagerAdapter {

        PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment f = null;

            switch(pos) {
                case 0:
                    f = LineChartFragment.createFor("Line Chart");
                    break;
                case 1:
                    f = BarChartFragment.createFor("Bar Chart");
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
