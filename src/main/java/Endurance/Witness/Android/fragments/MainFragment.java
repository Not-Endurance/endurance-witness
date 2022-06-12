package endurance.witness.android.fragments;

import endurance.witness.android.activities.MainActivity;
import endurance.witness.android.tools.UIHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.Android.R;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainFragment extends Fragment {
    private boolean isScanning = false;
    private MainActivity context;
    Handler handler;
    Button toggleButton;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = (MainActivity)getActivity();
        this.toggleButton = getView().findViewById(R.id.ToggleButton);
        this.toggleButton.setOnClickListener(new ToggleScanListener());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                long epoch = System.currentTimeMillis();
                long scannedAtEpoch = epoch - (SystemClock.uptimeMillis() - msg.getWhen());
                HttpTest test = new HttpTest();
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        // Probably want this because we need the process to run continiously regardless.
//        stopInventory();
    }

    private void toggleScanning() {
        String startString = context.getString(R.string.toggleButton);
        if (this.toggleButton.getText().equals(startString))
        {
            if (this.context.reader.startInventoryTag()) {
                String stopText = this.context.getString(R.string.title_stop_Inventory);
                this.toggleButton.setText(stopText);
                isScanning = true;
                new Scanner().start();
            } else {
                this.context.reader.stopInventory();
                UIHelper.ToastMessage(context, R.string.scan_start_error);
            }
        } else {
            if (this.isScanning) {
                this.isScanning = false;
                if (this.context.reader.stopInventory()) {
                    String startText = this.context.getString(R.string.toggleButton);
                    this.toggleButton.setText(startText);
                } else {
                    UIHelper.ToastMessage(context, R.string.scan_stop_error);
                }
            }
        }
    }

    class Scanner extends Thread {
        public void run() {
            String strTid;
            String strResult;
            while (isScanning) {
                UHFTAGInfo res = context.reader.readTagFromBuffer();
                if (res != null) {
                    strTid = res.getTid();
                    String sixteen = "0000000000000000";
                    String twentyFour = "000000000000000000000000";
                    if (strTid.length() != 0
                        && !strTid.equals(sixteen)
                        && !strTid.equals(twentyFour)
                    ) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "EPC:" + res.getEPC() + "@" + res.getRssi();
                    }

                    Message msg = handler.obtainMessage();
                    msg.obj = strResult;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    class HttpTest extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... params) {
            String result;
            String type;
            try {
                // HTTP
                URL url = new URL("http://google.com");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                int status = con.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                type = "success";
                result = content.toString();
            } catch (Exception ex) {
                type = "error";
                result = ex.getMessage();
            }
            return type;
        }
    }

    public class ToggleScanListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            toggleScanning();
        }
    }
}
