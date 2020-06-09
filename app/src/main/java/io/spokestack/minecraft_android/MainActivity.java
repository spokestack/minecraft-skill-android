package io.spokestack.minecraft_android;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.spokestack.minecraft_android.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements VoiceUI {
    // we want PREF_NAME to uniquely refer to our app
    private static final String PREF_NAME = "io.spokestack.minecraft_android";
    // VERSION_KEY should be unique within the app itself
    private static final String VERSION_KEY = "versionCode";
    private static final int NONEXISTENT = -1;

    private int audioPermCode = 1337;
    private String logTag = getClass().getSimpleName();
    private ActivityMainBinding viewBinding;
    private Spokestack spokestack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        // see if we were granted the microphone permission
        // during a previous session; if so, go ahead and build
        // Spokestack components
        if (checkMicPermission()) {
            buildSpokestack();
        }
    }

    @Override
    protected void onDestroy() {
        this.spokestack.stop();
        super.onDestroy();
    }

    private boolean checkMicPermission() {
        String recordAudio = Manifest.permission.RECORD_AUDIO;
        int granted = PackageManager.PERMISSION_GRANTED;
        if (ContextCompat.checkSelfPermission(this, recordAudio) == granted) {
            return true;
        }

        String[] permissions = new String[]{recordAudio};
        ActivityCompat.requestPermissions(this, permissions, audioPermCode);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(
          int requestCode,
          @NonNull String[] permissions,
          @NonNull int[] grantResults) {
        int granted = PackageManager.PERMISSION_GRANTED;
        if (requestCode == audioPermCode) {
            if (grantResults.length > 0 && grantResults[0] == granted) {
                buildSpokestack();
            } else {
                Log.w(logTag,
                      "Record permission not granted; voice control disabled!");
            }
        }
    }

    private void buildSpokestack() {
        // extract the models from the asset bundle if we need to
        checkForModels();
        this.spokestack = new Spokestack(getApplicationContext(),
              getLifecycle(), this);
        try {
            // start the speech pipeline
            this.spokestack.start();
            this.spokestack.launch();
        } catch (Exception e) {
            Log.e(logTag, "Problem starting Spokestack", e);
        }
    }

    private void checkForModels() {
        if (!modelsCached()) {
            decompressModels();
        } else {
            int currentVersionCode = BuildConfig.VERSION_CODE;
            SharedPreferences prefs =
                  getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            int savedVersionCode = prefs.getInt(VERSION_KEY, NONEXISTENT);

            if (currentVersionCode != savedVersionCode) {
                decompressModels();

                // Update the shared preferences with the current version code
                prefs.edit().putInt(VERSION_KEY, currentVersionCode).apply();
            }
        }
    }

    private boolean modelsCached() {
        String nluName = "nlu.tflite";
        File nluFile = new File(getCacheDir() + "/" + nluName);
        return nluFile.exists();
    }

    private void decompressModels() {
        try {
            cacheAsset("nlu.tflite");
            cacheAsset("metadata.json");
            cacheAsset("vocab.txt");
            cacheAsset("minecraft-recipe-metaphones.json");
        } catch (IOException e) {
            Log.e(logTag, "Unable to cache NLU data", e);
        }
    }

    private void cacheAsset(String assetName) throws IOException {
        File assetFile = new File(getCacheDir() + "/" + assetName);
        InputStream inputStream = getAssets().open(assetName);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        FileOutputStream fos = new FileOutputStream(assetFile);
        fos.write(buffer);
        fos.close();
    }

    public void activateAsrTapped(View view) {
        this.spokestack.activateAsr();
    }

    // UIDelegate methods, so the Spokestack class can communicate voice events

    @Override
    public void asrActivated() {
        runOnUiThread(() -> viewBinding.micButton.setEnabled(false));
        runOnUiThread(
              () -> viewBinding.utteranceLabel.setText(R.string.listen_text));
    }

    @Override
    public void asrDeactivated() {
        runOnUiThread(() -> viewBinding.micButton.setEnabled(true));
    }

    @Override
    public void transcriptAvailable(String transcript) {
        runOnUiThread(() -> viewBinding.utteranceLabel.setText(transcript));
    }

    @Override
    public void promptAvailable(String prompt) {
        runOnUiThread(() -> viewBinding.promptLabel.setText(prompt));
    }
}
