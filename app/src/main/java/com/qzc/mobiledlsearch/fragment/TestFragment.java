package com.qzc.mobiledlsearch.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huxq17.download.Pump;
import com.huxq17.download.core.DownloadInfo;
import com.huxq17.download.utils.LogUtil;
import com.ornach.nobobutton.NoboButton;
import com.qzc.mobiledlsearch.DownloadListActivity;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.Utils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TestFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private final HashMap<DownloadViewHolder, DownloadInfo> map = new HashMap<>();
    private RecyclerView recyclerView;
    private DownloadAdapter downloadAdapter;
    private List<DownloadInfo> downloadInfoList;

    public static TestFragment createFor(String text) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);

        recyclerView = view.findViewById(R.id.rvDownloadList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TestFragment.this.getActivity());
        //Get all download list
        downloadInfoList = Pump.getAllDownloadList();
        for (DownloadInfo downloadInfo : downloadInfoList) {
            LogUtil.e("id="+downloadInfo.getId()+";createTime="+downloadInfo.getCreateTime());
        }
        //Sort download list if need，default sort by createTime DESC
        Collections.sort(downloadInfoList, new Comparator<DownloadInfo>() {
            @Override
            public int compare(DownloadInfo o1, DownloadInfo o2) {
                return (int) (o1.getCreateTime() - o2.getCreateTime());
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        downloadAdapter = new DownloadAdapter(map, downloadInfoList);
        recyclerView.setAdapter(downloadAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (DownloadInfo downloadInfo : downloadInfoList) {
            Pump.stop(downloadInfo.getId());
        }
    }

    public class DownloadAdapter extends RecyclerView.Adapter<DownloadViewHolder> {
        List<DownloadInfo> downloadInfoList;
        HashMap<DownloadViewHolder, DownloadInfo> map;

        public DownloadAdapter(HashMap<DownloadViewHolder, DownloadInfo> map, List<DownloadInfo> downloadInfoList) {
            this.downloadInfoList = downloadInfoList;
            this.map = map;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_download_list, viewGroup, false);
            return new DownloadViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder viewHolder, int position) {
            DownloadInfo downloadInfo = downloadInfoList.get(position);
            downloadInfo.setExtraData(viewHolder);
            map.put(viewHolder, downloadInfo);
            viewHolder.bindData(downloadInfo);
        }

        public void delete(DownloadViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
            downloadInfoList.remove(position);
            notifyItemRemoved(position);
            map.remove(viewHolder);
        }

        @Override
        public int getItemCount() {
            return downloadInfoList.size();
        }
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder{
        ImageView ivDelete;
        ProgressBar progressBar;
        TextView tvName;
        TextView tvSpeed;
        TextView tvCreateTime;
        TextView tvDownload;
        NoboButton btnTest;
        DownloadInfo downloadInfo;
        AlertDialog dialogDelete;
        AlertDialog dialogTest;

        public DownloadViewHolder(@NonNull View itemView, final DownloadAdapter adapter) {
            super(itemView);

            ivDelete = itemView.findViewById(R.id.iv_icon);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDelete.show();
                }
            });
            dialogDelete = new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Confirm delete?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.delete(DownloadViewHolder.this);
                            Pump.deleteById(downloadInfo.getId());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();

            progressBar = itemView.findViewById(R.id.pb_progress);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSpeed = itemView.findViewById(R.id.tv_speed);
            tvDownload = itemView.findViewById(R.id.tv_download);
            tvCreateTime = itemView.findViewById(R.id.tvCreateTime);

            btnTest = itemView.findViewById(R.id.btn_test);
            btnTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogTest.show();
                }
            });
            dialogTest = new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Select Type?")
                    .setPositiveButton("Sensor Test", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment selectedScreen = TestDetailHARFragment.createFor("Test Detail");
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.container, selectedScreen);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    })
                    .setNegativeButton("Camera Test", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment selectedScreen = TestDetailCameraFragment.createFor("Test Detail");
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.container, selectedScreen);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    })
                    .create();
        }

        public void bindData(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            tvName.setText(downloadInfo.getName());
            String speed = "";
            int progress = downloadInfo.getProgress();
            progressBar.setProgress(progress);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String createTime =format.format(new Date(downloadInfo.getCreateTime()));
            tvCreateTime.setText(createTime);
            tvSpeed.setText(speed);
            long completedSize = downloadInfo.getCompletedSize();
            long totalSize = downloadInfo.getContentLength();
            tvDownload.setText(Utils.getDataSize(completedSize) + "/" + Utils.getDataSize(totalSize));
        }
    }
}
