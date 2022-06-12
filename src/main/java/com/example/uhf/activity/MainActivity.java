package com.example.uhf.activity;

import com.example.uhf.R;
import com.example.uhf.fragment.MainFragment;
import android.os.Bundle;

public class MainActivity extends FragmentBase {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);
        this.initUHF();
        this.initViewPager();
        this.initViewPageData();
	}

    @Override
	protected void onDestroy() {

		if (reader != null) {
			this.reader.free();
		}
		super.onDestroy();
	}

    protected void initViewPageData() {
        this.fragments.add(new MainFragment());
    }
}
