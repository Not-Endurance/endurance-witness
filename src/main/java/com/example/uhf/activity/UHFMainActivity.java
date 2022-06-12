package com.example.uhf.activity;

import com.example.uhf.R;
import com.example.uhf.fragment.UHFReadTagFragment;
import android.os.Bundle;

public class UHFMainActivity extends BaseTabFragmentActivity {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUHF();
        initViewPager();
        this.initViewPageData();
	}

    @Override
	protected void onDestroy() {

		if (mReader != null) {
			mReader.free();
		}
		super.onDestroy();
	}

    protected void initViewPageData() {
        fragments.add(new UHFReadTagFragment());
    }
}
