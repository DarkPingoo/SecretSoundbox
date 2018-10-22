package fr.eseo.dis.tristan.secretsoundbox;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


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

    private void checkForUpdate() {
        Log.d("Secret", "Check for update");
        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setUpdateFrom(UpdateFrom.JSON);
        appUpdater.setUpdateJSON("https://bitbucket.org/DarkPingoo11/secretsoundbox/raw/HEAD/release/update-changelog.json");
        appUpdater.start();
    }
}
