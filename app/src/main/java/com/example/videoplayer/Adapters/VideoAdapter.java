package com.example.videoplayer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videoplayer.Activities.MainActivity;
import com.example.videoplayer.Activities.VideoPlayerActivity;
import com.example.videoplayer.Models.VideoModel;
import com.example.videoplayer.R;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    Context context;
    ArrayList<VideoModel> videoModels;

    public VideoAdapter(Context context, ArrayList<VideoModel> videoModels) {
        this.context = context;
        this.videoModels = videoModels;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.video_list, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        final VideoModel model = videoModels.get(position);

        Glide.with(context).load(model.getVideo_path()).into(holder.videoThumb);
        holder.videoName.setText(model.getVideo_name());
        holder.videoDuration.setText(model.getVideo_duration());
        holder.folderName.setText(model.getFolder_name());
        holder.videoSize.setText(model.getVideo_size());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoPath", model.getVideo_path());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoModels.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView videoName, videoDuration, folderName, videoSize;
        ImageView videoThumb;
        RelativeLayout relativeLayout;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoDuration = itemView.findViewById(R.id.videoDuration);
            videoName = itemView.findViewById(R.id.videoName);
            folderName = itemView.findViewById(R.id.folderName);
            videoSize = itemView.findViewById(R.id.videoSize);
            videoThumb = itemView.findViewById(R.id.videoThumb);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

        }
    }
}
