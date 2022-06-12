package com.example.uhf.fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.uhf.R;
import com.example.uhf.activity.UHFMainActivity;
import com.example.uhf.tools.UIHelper;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UHFReadTagFragment extends KeyDwonFragment {
    private boolean loopFlag = false;
    Handler handler;
    Button BtInventory;
    private UHFMainActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onCreateView");
        return inflater
                .inflate(R.layout.uhf_readtag_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        New
        mContext = (UHFMainActivity) getActivity();
        BtInventory = getView().findViewById(R.id.BtInventory);
        BtInventory.setOnClickListener(new ToggleScanListener());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
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

    private void readTag() {
        String startString = mContext.getString(R.string.btInventory);
        if (BtInventory.getText().equals(startString))// 识别标签
        {
            if (mContext.mReader.startInventoryTag()) {
                BtInventory.setText(
                        mContext
                        .getString(R.string.title_stop_Inventory));
                loopFlag = true;
                new TagThread().start();
            } else {
                mContext.mReader.stopInventory();
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_inventory_open_fail);
            }
        } else {// 停止识别
            stopInventory();
        }
    }

    /**
     * 停止识别
     */
    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            if (mContext.mReader.stopInventory()) {
                BtInventory.setText(mContext.getString(R.string.btInventory));
            } else {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_inventory_stop_fail);
            }
        }
    }

    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult;
            UHFTAGInfo res = null;
            while (loopFlag) {
                res = mContext.mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res.getTid();
                    if (strTid.length() != 0 && !strTid.equals("0000000" +
                            "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Log.i("data","EPC:"+res.getEPC()+"|"+strResult);
                    Message msg = handler.obtainMessage();
                    msg.obj = strResult + "EPC:" + res.getEPC() + "@" + res.getRssi();

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

    @Override
    public void myOnKeyDwon() {
        readTag();
    }


    public class ToggleScanListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            readTag();
        }
    }
}
