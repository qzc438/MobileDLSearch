package com.qzc.mobiledlsearch.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.utils.DecodeBitmapTask;


public class ImageFragment extends Fragment implements DecodeBitmapTask.Listener{

    static final String BUNDLE_IMAGE_ID = "BUNDLE_IMAGE_ID";

    private CardView cardView;

    private ImageView imageView;
    private DecodeBitmapTask decodeBitmapTask;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image, container, false);
        cardView = (CardView) view.findViewById(R.id.card);
        Bundle args = getArguments();
        final int smallResId = args != null ? args.getInt(BUNDLE_IMAGE_ID) : 0;
        if (smallResId == -1) {
            ImageFragment.this.getActivity().finish();
        }

        imageView = (ImageView)view.findViewById(R.id.image);
        imageView.setImageResource(smallResId);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImageFragment.this.getActivity().onBackPressed();
                getFragmentManager().beginTransaction().replace(R.id.container, new OverviewFragment()).commit();
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            loadFullSizeBitmap(smallResId);
        } else {
            ImageFragment.this.getActivity().getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {

                private boolean isClosing = false;

                @Override
                public void onTransitionPause(Transition transition) {}
                @Override
                public void onTransitionResume(Transition transition) {}
                @Override
                public void onTransitionCancel(Transition transition) {}

                @Override
                public void onTransitionStart(Transition transition) {
                    if (isClosing) {
                        addCardCorners();
                    }
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    if (!isClosing) {
                        isClosing = true;

                        removeCardCorners();
                        loadFullSizeBitmap(smallResId);
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ImageFragment.this.getActivity().isFinishing() && decodeBitmapTask != null) {
            decodeBitmapTask.cancel(true);
        }
    }

    private void addCardCorners() {
        cardView.setRadius(25f);
    }

    private void removeCardCorners() {
        ObjectAnimator.ofFloat(cardView, "radius", 0f).setDuration(50).start();
    }

    private void loadFullSizeBitmap(int smallResId) {
        int bigResId;
        switch (smallResId) {
            case R.drawable.p1: bigResId = R.drawable.p1_big; break;
            case R.drawable.p2: bigResId = R.drawable.p2_big; break;
            case R.drawable.p3: bigResId = R.drawable.p3_big; break;
            case R.drawable.p4: bigResId = R.drawable.p4_big; break;
            case R.drawable.p5: bigResId = R.drawable.p5_big; break;
            default: bigResId = R.drawable.p1_big;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        ImageFragment.this.getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);

        final int w = metrics.widthPixels;
        final int h = metrics.heightPixels;

        decodeBitmapTask = new DecodeBitmapTask(getResources(), bigResId, w, h, this);
        decodeBitmapTask.execute();
    }

    @Override
    public void onPostExecuted(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
