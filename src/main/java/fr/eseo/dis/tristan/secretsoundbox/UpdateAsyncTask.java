package fr.eseo.dis.tristan.secretsoundbox;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Tristan LE GACQUE
 * Created 22/10/2018
 */
public class UpdateAsyncTask extends AsyncTask<Void, Integer, Void> {

    private final String url;
    private final String path;
    private final Application app;

    public UpdateAsyncTask(String url, String path, Application app) {
        this.url = url;
        this.path = path;
        this.app = app;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(this.url);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(this.path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("SecretSoundbox", "La mise a jour de l'appli ne s'est pas déroulée comme prévu");
            Log.e("SecretSoundbox", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(this.path)), "application/vnd.android.package-archive" );
        Log.d("SecretSoundbox", "About to install the new .apk");
        this.app.startActivity(i);
    }

}
