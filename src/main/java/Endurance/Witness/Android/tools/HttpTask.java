package endurance.witness.android.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpTask extends AsyncTask<String, Void, Integer> {
    private String url;
    private Context context;

    HttpTask(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            if (params.length != 2) {
                throw new Exception("Invalid params: expected 2 - tag ID and epoc timestamp");
            }
            String tagId = params[0];
            String epochStamp = params[1];
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches (false);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id", tagId);
            jsonParam.put("epoch", epochStamp);
            wr.writeBytes(jsonParam.toString());

            wr.flush();
            wr.close();

            int status = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
