package com.example.uhf.activity;

import com.example.uhf.R;
import com.example.uhf.fragment.UHFReadTagFragment;
import android.os.Bundle;

public class UHFMainActivity extends BaseTabFragmentActivity {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
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
        this.fragments.add(new UHFReadTagFragment());
    }
}
