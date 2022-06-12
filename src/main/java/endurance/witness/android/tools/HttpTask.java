package endurance.witness.android.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpTask extends AsyncTask<Void, Void, Integer> {
    private String url;
    private Context context;

    HttpTask(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            in.close();
            return status;
        } catch (Exception ex) {
            Log.e("HTTP error:", ex.toString());
            return 999;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        String message;
        if (status > 399) {
            message = "error: " + status;
        } else {
            message = "success: " + status;
        }
        UIHelper.ToastMessage(this.context, message);
    }
}
