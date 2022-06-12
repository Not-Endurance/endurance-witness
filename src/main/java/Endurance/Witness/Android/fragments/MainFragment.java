package endurance.witness.android.fragments;

import endurance.witness.android.activities.MainActivity;
import endurance.witness.android.tools.HttpHandler;
import endurance.witness.android.tools.RfidScanner;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import com.example.Android.R;

public class MainFragment extends Fragment {
    private MainActivity context;
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
        this.connectButton= view.findViewById(R.id.connectButton);
        this.scanButton = view.findViewById(R.id.scanButton);
        this.witnessRoles = view.findViewById(R.id.witnessRoles);

        RfidScanner rfidScanner = new RfidScanner(this.context, scanHandler);
        this.scanButton.setOnClickListener(new ToggleScanListener(rfidScanner, this.scanButton));
        this.connectButton.setOnClickListener(new ConnectListener());

        this.scanHandler = new HttpHandler(this.context, "https://google.com");
    }

    @Override
    public void onPause() {
        super.onPause();

        // Probably want this because we need the process to run continiously regardless.
//        stopInventory();
    }

    public class ToggleScanListener implements OnClickListener {
        private RfidScanner scanner;
        private Button button;
        ToggleScanListener(RfidScanner scanner, Button button) {
            this.scanner = scanner;
            this.button = button;
        }

        @Override
        public void onClick(View v) {
            boolean hasStarted = this.scanner.toggle();
            String text = hasStarted ? "Stop" : "Start";
            button.setText(text);
        }
    }

    public class ConnectListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String url = "https://google.com";
            Handler handler = new HttpHandler(context, url);
            Message message = handler.obtainMessage();
            handler.dispatchMessage(message);
        }
    }
}
