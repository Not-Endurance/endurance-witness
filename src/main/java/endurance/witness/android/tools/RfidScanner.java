package endurance.witness.android.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.example.Android.R;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;

public class RfidScanner {
    private Context context;
    private RFIDWithUHFUART reader;
    private ScannerThread scanner;
    private boolean isScanning = false;
    private Handler handler;

    public RfidScanner(Context context, Handler handler) {
        try {
            this.reader = RFIDWithUHFUART.getInstance();
            new InitTask(this.reader).execute();
        } catch (Exception ex) {
            UIHelper.ToastMessage(this.context, ex.getMessage());
        }

        this.context = context;
        this.handler = handler;
    }

    public Boolean toggle() {
        if (this.isScanning)
        {
            boolean hasStopped = this.reader.stopInventory();
            if (!hasStopped) {
                UIHelper.ToastMessage(context, R.string.scan_stop_error);
            }
            return hasStopped;
        } else {
            ScannerThread scanner = new ScannerThread(this.reader, this.handler);
            scanner.start();
            boolean isRfidScanning = this.reader.startInventoryTag();
            if (!isRfidScanning) {
                UIHelper.ToastMessage(context, R.string.scan_start_error);
            }
            this.isScanning = isRfidScanning;
            return this.isScanning;
        }
    }

    private class InitTask extends AsyncTask<String, Integer, Boolean> {
        private RFIDWithUHFUART reader;

        InitTask(RFIDWithUHFUART reader) {
            this.reader = reader;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return this.reader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                UIHelper.ToastMessage(context, "init fail");
            }
        }
    }

    private class ScannerThread extends Thread {
        private Handler handler;
        private RFIDWithUHFUART reader;

        ScannerThread(RFIDWithUHFUART reader, Handler handler) {
            this.handler = handler;
            this.reader = reader;
        }

        public void run() {
            String strTid;
            String strResult;
            while (isScanning) {
                UHFTAGInfo res = this.reader.readTagFromBuffer();
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

                    Message msg = this.handler.obtainMessage();
                    msg.obj = strResult;
                    this.handler.sendMessage(msg);
                }
            }
        }
    }
}
