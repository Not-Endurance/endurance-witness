package endurance.witness.android.fragments;

import endurance.witness.android.activities.MainActivity;
import endurance.witness.android.tools.JudgeClient;
import endurance.witness.android.tools.UIHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.Android.R;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.util.Objects;

public class MainFragment extends Fragment {
    private boolean isScanning = false;
    private MainActivity context;
    EditText judgeIpText;
    Button scanButton;
    Handler scanHandler;
    Button connectButton;
    Spinner witnessRoles;

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
        View view = getView();
        this.judgeIpText = view.findViewById(R.id.judgeIpText);
        this.connectButton= view.findViewById(R.id.connectButton);
        this.scanButton = view.findViewById(R.id.scanButton);
        this.witnessRoles = view.findViewById(R.id.witnessRoles);

        this.scanButton.setOnClickListener(new ToggleScanListener());
        this.connectButton.setOnClickListener(new ConnectListener());
    }

    @Override
    public void onPause() {
        super.onPause();

        // Probably want this because we need the process to run continiously regardless.
//        stopInventory();
    }

    private void toggleScanning() {
        String startString = context.getString(R.string.toggleButton);
        Editable ipAddressEditable = this.judgeIpText.getText();
        if (ipAddressEditable == null || ipAddressEditable.toString().equals("")) {
            UIHelper.ToastMessage(context, "Specify IP address");
            return;
        }
        JudgeClient.IpAddress = ipAddressEditable.toString();
        if (this.scanButton.getText().equals(startString))
        {
            Object role = this.witnessRoles.getSelectedItem();
            this.scanHandler = new JudgeClient(this.context, role.toString());
            if (this.context.reader.startInventoryTag()) {
                String stopText = this.context.getString(R.string.title_stop_Inventory);
                this.scanButton.setText(stopText);
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
                    this.scanButton.setText(startText);
                } else {
                    UIHelper.ToastMessage(context, R.string.scan_stop_error);
                }
            }
        }
    }

    class Scanner extends Thread {
        public void run() {
            while (isScanning) {
                UHFTAGInfo res = context.reader.readTagFromBuffer();
                if (res != null) {
                    Message msg = scanHandler.obtainMessage();
                    msg.obj = res.getEPC();
                    scanHandler.sendMessage(msg);
                }
            }
        }
    }

    public class ToggleScanListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            toggleScanning();
        }
    }

    public class ConnectListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Object role = witnessRoles.getSelectedItem();
            Handler handler = new JudgeClient(context, role.toString());
            Message message = handler.obtainMessage();
            message.obj = "test";
            handler.dispatchMessage(message);
        }
    }
}
