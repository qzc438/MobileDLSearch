package com.qzc.mobiledlsearch;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qzc.mobiledlsearch.fragment.DetailDataFragment;
import com.qzc.mobiledlsearch.fragment.DetailLayerFragment;
import com.qzc.mobiledlsearch.fragment.DetailModelFragment;
import com.qzc.mobiledlsearch.fragment.OverviewFragment;
import com.qzc.mobiledlsearch.fragment.SearchFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.qzc.mobiledlsearch.menu.DrawerAdapter;
import com.qzc.mobiledlsearch.menu.DrawerItem;
import com.qzc.mobiledlsearch.menu.SimpleItem;
import com.qzc.mobiledlsearch.menu.SpaceItem;

import java.util.Arrays;

/**
 * Created by yarolegovich on 25.03.2017.
 */

public class SampleActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_FILTER = 0;
    private static final int POS_OVERVIEW = 1;
    private static final int POS_DETAIL = 2;
    private static final int POS_TEST = 3;
    private static final int POS_LOGOUT = 5;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withRootViewScale(0.7f)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_FILTER).setChecked(true),
                createItemFor(POS_OVERVIEW),
                createItemFor(POS_DETAIL),
                createItemFor(POS_TEST),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_FILTER);
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            finish();
        }
        slidingRootNav.closeMenu();
//        Fragment selectedScreen = CenteredTextFragment.createFor(screenTitles[position]);
//        showFragment(selectedScreen);
        if (position == POS_FILTER) {
            Fragment selectedScreen = SearchFragment.createFor(screenTitles[position]);
            showFragment(selectedScreen);
        }
        if (position == POS_OVERVIEW) {
            Fragment selectedScreen = OverviewFragment.createFor(screenTitles[position]);
            showFragment(selectedScreen);
        }
        if (position == POS_DETAIL) {
            Fragment selectedScreen = DetailModelFragment.createFor(screenTitles[position], "Model");
            showFragment(selectedScreen);
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.white))
                .withTextTint(color(R.color.white))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
}

