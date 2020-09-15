package com.example.videoplayer.Models;

public class VideoModel {

    private String video_path, video_duration, video_size, video_name, folder_name;

    public VideoModel(String video_path, String video_duration, String video_size, String video_name, String folder_name) {
        this.video_path = video_path;
        this.video_duration = video_duration;
        this.video_size = video_size;
        this.video_name = video_name;
        this.folder_name = folder_name;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public String getVideo_path() {
        return video_path;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public String getVideo_size() {
        return video_size;
    }

    public String getVideo_name() {
        return video_name;
    }
}
