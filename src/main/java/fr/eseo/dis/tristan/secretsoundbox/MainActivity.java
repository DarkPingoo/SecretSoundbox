package fr.eseo.dis.tristan.secretsoundbox;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dcastalia.localappupdate.DownloadApk;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 2;
    private static final int MY_PERMISSIONS_REQUEST_REQUEST = 3;
    private boolean playing;
    private Map<Button, Integer> musicMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.musicMap = new HashMap<>();

        this.musicMap.put((Button) findViewById(R.id.button_nein), R.raw.nein);
        this.musicMap.put((Button) findViewById(R.id.button_ja), R.raw.ja);
        this.musicMap.put((Button) findViewById(R.id.button_fascita), R.raw.fascista);
        this.musicMap.put((Button) findViewById(R.id.button_douel), R.raw.douel);

        // Définir le click
        for(Button b : this.musicMap.keySet()) {
            b.setOnClickListener(this);
            this.setButtonStatus(b, false);
        }

        checkForUpdate();
    }


    @Override
    public void onClick(View v) {
        if(v instanceof Button) {
            playMusic((Button) v);
        }
    }

    private void playMusic(final Button b) {
        Integer idToPlay = this.musicMap.get(b);

        if(idToPlay != null && !playing) {
            playing = true;
            this.setButtonStatus(b, true);

            //Play music
            //this.setMusicToMax();
            final MediaPlayer mp = MediaPlayer.create(this, idToPlay);

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setButtonStatus(b, false);
                    playing = false;
                }
            });

            mp.start();
        }
    }

    private void setMusicToMax() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
    }

    private void setButtonStatus(Button b, boolean status) {
        Integer color = status ? R.color.colorPrimary : R.color.colorDefault;
        b.setBackgroundColor(this.getApplicationContext().getResources().getColor(color));
    }


    /**
     * Vérifie si une mise a jour est disponible
     * Si une mise a jour est disponible, on propose de l'installer
     */
    private void checkForUpdate() {
        Log.d("Secret", "Check for update");
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://github.com/DarkPingoo/SecretSoundbox/raw/master/release/update-changelog.json")
                .withListener(new AppUpdaterUtils.UpdateListener() {

                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        // String url = update.getUrlToDownload()+"";
                        Log.d("Secret", "Update ? " + isUpdateAvailable);
                        Log.d("Secret", "Version ? " + update.getLatestVersionCode() + " | " + update.getLatestVersion());
                        if(isUpdateAvailable) {
                            showDialog(update.getLatestVersion(), update.getUrlToDownload()+"");
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) { }
                });
        appUpdaterUtils.start();
    }

    /**
     * Mise a jour de l'application
     */
    private void updateApp(String url) {
        this.checkForPermissions();
        Log.d("Secret", "Téléchargement depuis l'url : " + url);

        DownloadApk downloadApk = new DownloadApk(MainActivity.this);
        downloadApk.startDownloadingApk(url);
    }


    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermissions() {
        if(!this.hasPermission(Manifest.permission.INTERNET)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }

        if(!this.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE);
        }

        if(!this.hasPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                    MY_PERMISSIONS_REQUEST_REQUEST);
        }
    }

    /**
     * Affiche le dialogue de mise a jour de l'application
     * @param version Version a mettre à jour
     */
    public void showDialog(String version, final String url) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.appupdater_update_available)
                .setMessage(this.getString(R.string.appupdater_update_available_description_dialog, version, "SecretSoundbox"))
                .setPositiveButton(R.string.appupdater_btn_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        updateApp(url);
                    }
                })
                .setNegativeButton(R.string.appupdater_btn_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}
