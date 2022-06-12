package endurance.witness.android.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class HttpHandler extends Handler {
    private String url;
    private Context context;

    public HttpHandler(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    @Override
    public void handleMessage(Message message) {
        long epoch = System.currentTimeMillis();
        long scannedAtEpoch = epoch - (SystemClock.uptimeMillis() - message.getWhen());
        HttpTask task = new HttpTask(this.context, this.url);
        task.execute();
    }
}
