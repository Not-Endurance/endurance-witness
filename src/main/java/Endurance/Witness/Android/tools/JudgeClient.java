package endurance.witness.android.tools;

import android.content.Context;
import android.os.Message;
import android.os.SystemClock;

import com.example.Android.R;

public class JudgeClient extends HttpHandler {
    public static String IpAddress;

    public JudgeClient(Context context, String role) {
        super(context, BuildUrl(role, context));
    }

    // TODO: use dynamic value for port
    private static String BuildUrl(String role, Context context) {
        String host = "http://" + IpAddress + ":11337/witness";
        String vetRole = context.getString(R.string.witness_role_vet);
        String endpoint = host + "/";
        if (role.equals(vetRole)) {
            endpoint += JudgeApi.vet;
        } else {
            endpoint += JudgeApi.finish;
        }
        return endpoint;
    }

    @Override
    public void handleMessage(Message message) {
        long epoch = System.currentTimeMillis();
        long scannedAtEpoch = epoch - (SystemClock.uptimeMillis() - message.getWhen());
        HttpTask task = new HttpTask(this.context, this.url);
        task.execute(message.obj.toString(), Long.toString(scannedAtEpoch));
    }

    private static class JudgeApi {
        static String vet = "vet";
        static String finish = "finish";
    }
}
