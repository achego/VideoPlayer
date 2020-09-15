package com.example.videoplayer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.example.videoplayer.Adapters.VideoAdapter;
import com.example.videoplayer.Models.VideoModel;
import com.example.videoplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    int[] requestCode = {100};

    String filepath = "";

    ArrayList<VideoModel> videoModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.videorecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        videoModels = new ArrayList<>();

        //askPermission();


        //File videoFolder = new File(Environment.getExternalStorageDirectory()+File.separator+filepath);
        //File[] videoFiles = videoFolder.listFiles();
        
        final File rootFolder = new File(Environment.getExternalStorageDirectory()+File.separator+filepath);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                loopFolders(rootFolder);
            }
        }).start();

//        if(videoFiles != null & videoFiles.length > 0) {
//
//            for (File file : videoFiles) {
//
//                if (file.getName().endsWith(".mp4") | file.getName().endsWith(".avi") | file.getName().endsWith(".3gp")) {
//
//                    VideoModel model = new VideoModel(file.getPath(), getDuration(file), "0 MB", file.getName());
//                    videoModels.add(model);
//
//                }
//
//            }
//
//        }

        VideoAdapter videoAdapter = new VideoAdapter(this, videoModels);
        recyclerView.setAdapter(videoAdapter);


    }

    private void askPermission() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !Settings.canDrawOverlays(this)){

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package"+getPackageName()));
            startActivityForResult(intent, 2004);

        }
        else{
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package"+getPackageName()));
            startActivityForResult(intent, 2004);
        }

    }

    private void loopFolders(File rootFolder) {

        if(rootFolder.isDirectory() & rootFolder.listFiles()!= null & rootFolder.listFiles().length > 0){

            File[] files = rootFolder.listFiles();

            for(File file : files){

                if(file.isDirectory()){
                    loopFolders(file);
                }
                else{
                    //System.out.println("in Folder " + file.getParentFile().getName() + " : " + file.getName());
                    if(file.getName().endsWith(".mp4") | file.getName().endsWith(".avi") | file.getName().endsWith(".3gp")){

                        VideoModel model = new VideoModel(file.getPath(), getDuration(file), getSize(file),
                                file.getName(), file.getParentFile().getName());
                        videoModels.add(model);

                    }
                }

            }

        }
        else{
            if(rootFolder.getName().endsWith(".mp4") | rootFolder.getName().endsWith(".avi") | rootFolder.getName().endsWith(".3gp")){

                VideoModel model = new VideoModel(rootFolder.getPath(), getDuration(rootFolder), "0 MB",
                        rootFolder.getName(), rootFolder.getParentFile().getName());
                videoModels.add(model);
            }
        }

    }

    private String getSize(File file) {

        long byteSize = file.length();
        String size = "";
        if(byteSize > 999999){

            int mb = (int)(byteSize/1000000.0);
            int kb = (int)(((byteSize/1000000.0) - mb) * 100);
            //int kb = Math.round(((byteSize/10000.0) - mb) * 100));
            size = mb+"."+kb+" MB";

        }
        else{

            int kb = (int)(byteSize/1000.0);
            int by = (int)(((byteSize/1000.0) - kb) * 100);
            size = kb+"."+by+" KB";

        }

        return size;

    }

    private String getDuration(File video) {

        MediaPlayer media = MediaPlayer.create(this, Uri.parse(video.getAbsolutePath()));
        int duration = media.getDuration();
        media.release();
        //return duration;
        //return millsecToFormatTime(duration);
        return millsecToFormatTime2(duration);

    }

    private String millsecToFormatTime(int duration) {

        long min = duration/(1000*60);
        int minutes = (int)min;
        int seconds = (int) ((min - minutes) * 60);

        String formatTime;
        if(minutes > 60){
            int hour = (int)(minutes/60);
            minutes = minutes - 60;
            formatTime = String.valueOf(hour+":"+minutes+":"+seconds);
        }
        else{
            formatTime = String.valueOf(minutes+":"+seconds);
        }
        return formatTime;

    }

    private String millsecToFormatTime2(int duration) {

        int hour = (int) TimeUnit.MILLISECONDS.toHours(duration);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(duration) - 60);
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) * 60));
        String time = "";

        if(hour > 0){
            time = String.format("%d:%d:%d", hour, minutes, seconds);
        }
        else{
            time = String.format("%d:%d", minutes, seconds);
        }

        return time;

    }

    private void grantPermission(String permision, int requestcode) {

        if(ContextCompat.checkSelfPermission(this, permision) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[] {permision}, requestcode);

        }
        else{

            Toast.makeText(this, "permission already Granted", Toast.LENGTH_SHORT).show();

        }

    }
}
