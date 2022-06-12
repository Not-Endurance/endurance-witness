package endurance.witness.android.activities;

import com.example.Android.R;
import android.os.Bundle;
import endurance.witness.android.fragments.FragmentBase;
import endurance.witness.android.fragments.MainFragment;

public class MainActivity extends FragmentBase {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);
        this.initViewPager();
        this.initViewPageData();
	}

    protected void initViewPageData() {
        this.fragments.add(new MainFragment());
    }
}
