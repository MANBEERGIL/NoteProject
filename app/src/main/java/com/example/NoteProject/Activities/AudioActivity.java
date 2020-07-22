package com.example.NoteProject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.NoteProject.R;
import com.example.NoteProject.Singelton.AudioSingleton;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AudioActivity extends AppCompatActivity {

    Button btnRecod, btnStop, btnPlay, btnSave, btn_stop_rec;
    String audioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "audio_file";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    String audioURL;

    Boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        btnPlay = findViewById(R.id.btnPlay);
        btnRecod = findViewById(R.id.btnRecord);
        btnStop =  findViewById(R.id.btnStop);
        btnSave  = findViewById(R.id.btn_save);
        btn_stop_rec = findViewById(R.id.btn_stop_rec);
        random = new Random();
        Intent intent = getIntent();
        audioURL = intent.getExtras().getString("audiourl");
        isEdit = getIntent().getExtras().getBoolean("isEdit");

        if (isEdit){
            if (audioURL!=null){
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnSave.setEnabled(false);
                btn_stop_rec.setEnabled(false);
                audioSavePathInDevice = audioURL;
            }else {
                btnPlay.setEnabled(false);
                btnStop.setEnabled(false);
                btn_stop_rec.setEnabled(false);
                btnSave.setEnabled(false);
                RandomAudioFileName = "audio_file";
            }

        }else{
            btnStop.setEnabled(false);
            btnPlay.setEnabled(false);
            btnSave.setEnabled(false);
            btn_stop_rec.setEnabled(false);
            RandomAudioFileName = "audio_file";
        }

        btnRecod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {

                    if(checkPermission()) {

                        audioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                        CreateRandomAudioFileName(5) + "DailyNote.3gp";
//
                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                        btnRecod.setEnabled(true);
                        btn_stop_rec.setEnabled(true);
                        // btnStop.setEnabled(true);


                        Toast.makeText(AudioActivity.this, "Recording started",
                                Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }

                }
            }
        });

        btn_stop_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaRecorder!=null){
                    mediaRecorder.stop();
                    btnStop.setEnabled(false);
                    btn_stop_rec.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnRecod.setEnabled(true);
                    btnSave.setEnabled(true);
                    Toast.makeText(AudioActivity.this, "Recording Completed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null){
                    mediaPlayer.stop();
                    btnStop.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnRecod.setEnabled(true);
                    Toast.makeText(AudioActivity.this, "Play Completed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    btnStop.setEnabled(true);
                    btnRecod.setEnabled(false);
                    btnPlay.setEnabled(false);
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(audioSavePathInDevice);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioSingleton audioSingleton = AudioSingleton.getInstance();
                audioSingleton.setAudioUrl(audioSavePathInDevice);

                finish();
            }
        });


    }



    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));
            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AudioActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AudioActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AudioActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}