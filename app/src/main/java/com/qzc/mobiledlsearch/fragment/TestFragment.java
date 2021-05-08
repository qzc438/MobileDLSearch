package com.qzc.mobiledlsearch.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huxq17.download.Pump;
import com.huxq17.download.core.DownloadInfo;
import com.huxq17.download.utils.LogUtil;
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

    public static class DownloadAdapter extends RecyclerView.Adapter<DownloadViewHolder> {
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

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView tvName;
        TextView tvStatus;
        TextView tvSpeed;
        TextView tvCreateTime;
        TextView tvDownload;
        DownloadInfo downloadInfo;
        DownloadInfo.Status status;
        AlertDialog dialog;

        public DownloadViewHolder(@NonNull View itemView, final DownloadAdapter adapter) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.pb_progress);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.bt_status);
            tvSpeed = itemView.findViewById(R.id.tv_speed);
            tvDownload = itemView.findViewById(R.id.tv_download);
            tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
            dialog = new AlertDialog.Builder(itemView.getContext())
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
        }

        public void bindData(DownloadInfo downloadInfo) {

            this.downloadInfo = downloadInfo;
            this.status = downloadInfo.getStatus();
            tvName.setText(downloadInfo.getName());
            String speed = "";
            int progress = downloadInfo.getProgress();
            progressBar.setProgress(progress);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String createTime =format.format(new Date(downloadInfo.getCreateTime()));
            tvCreateTime.setText(createTime);

            switch (status) {
                case STOPPED:
                    tvStatus.setText("Start");
                    break;
                case PAUSING:
                    tvStatus.setText("Pausing");
                    break;
                case PAUSED:
                    tvStatus.setText("Continue");
                    break;
                case WAIT:
                    tvStatus.setText("Waiting");
                    break;
                case RUNNING:
                    tvStatus.setText("Pause");
                    speed = downloadInfo.getSpeed();
                    break;
                case FINISHED:
                    tvStatus.setText("Test");
                    break;
                case FAILED:
                    tvStatus.setText("Retry");
                    break;
            }

            tvSpeed.setText(speed);

            long completedSize = downloadInfo.getCompletedSize();
            long totalSize = downloadInfo.getContentLength();
            tvDownload.setText(Utils.getDataSize(completedSize) + "/" + Utils.getDataSize(totalSize));
        }
    }
}
