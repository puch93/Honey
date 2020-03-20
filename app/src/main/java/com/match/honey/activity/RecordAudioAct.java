package com.match.honey.activity;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.view.View;

import com.match.honey.R;
import com.match.honey.databinding.ActivityRecordaudioBinding;

import java.io.File;
import java.io.IOException;

public class RecordAudioAct extends Activity implements View.OnClickListener{

    ActivityRecordaudioBinding binding;

    MediaRecorder recorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recordaudio);

        binding.btnRecord.setOnClickListener(this);
        binding.btnStop.setOnClickListener(this);

        recorder = new MediaRecorder();
    }

    private File getOutputMediaFile(){
//        String recordPath = getExternalCacheDir().getAbsolutePath();
        String recordPath = Environment.getExternalStorageDirectory() + "/MARRYME";
//        String recordPath = Environment.getExternalStorageDirectory() + "/MARRYME/";
//        File mediaFile = new File(recordPath + File.separator + "audio.amr");
//        File mediaFile = new File(recordPath + File.separator + "audio.m4a");
        File mediaFile = new File(recordPath + File.separator + "audio.m4a");
        return mediaFile;
    }

    private void startRec(){
        String recordFilePath = getOutputMediaFile().getAbsolutePath();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(recordFilePath);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    private void stopRec(){
        recorder.stop();
        recorder.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_record:
                startRec();
                break;
            case R.id.btn_stop:
                stopRec();
                break;
        }
    }
}
