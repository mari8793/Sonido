package com.example.mari.sonido;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {

    //Codigos de los permisos
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;


    //VARIABLES
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static String audioFilePath;
    private static Button stopButton;
    private static Button playButton;
    private static Button recordButton;
    private boolean isRecording;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //traemos los botones y checamos si se tiene microfono en el cel

        recordButton = (Button) findViewById(R.id.record);
        playButton = (Button) findViewById(R.id.play);
        stopButton = (Button) findViewById(R.id.stop);

        if(!hasMicrophone()){
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
        }
        else{
            playButton.setEnabled(false);
            stopButton.setEnabled(false);

        }
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/lupe.3gp";

        //pedimos el permiso
        requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_REQUEST_CODE);

    }
    protected boolean hasMicrophone(){
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);

    }

    public void recordAudio(View view) throws IOException{
        isRecording= true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        mediaRecorder.start();

    }//record

    public void stopAudio(View view){
        stopButton.setEnabled(false);
        playButton.setEnabled(true);
        if(isRecording){
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

        }else{

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }

    public void playAudio(View view) throws IOException{
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }




    protected void requestPermission(String permissionType, int requestCode){
        int permission = ContextCompat.checkSelfPermission(this, permissionType);
        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String []{permissionType}, requestCode);
        }

    } //permiso

    @Override
    public void onRequestPermissionsResult(int requestCode, String perimissions[], int[] grantResults){
        switch (requestCode){
            case RECORD_REQUEST_CODE:{
                if(grantResults.length==0 || grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    recordButton.setEnabled(false);
                    Toast.makeText(this, "Permiso de grabación", Toast.LENGTH_LONG).show();
                }else{
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE);
                }
                return;

            }

            case STORAGE_REQUEST_CODE:{
                if(grantResults.length==0|| grantResults[0]!= PackageManager.PERMISSION_GRANTED) {
                    recordButton.setEnabled(false);
                    Toast.makeText(this, "Permiso de Almacenamiento requerido", Toast.LENGTH_LONG).show();
                }
                return;
                }
            }
        }
    }

