package com.example.uhf.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.example.uhf.R;
import com.example.uhf.adapter.ViewPagerAdapter;
import com.example.uhf.widget.NoScrollViewPager;
import com.rscja.deviceapi.RFIDWithUHFUART;

public class BaseTabFragmentActivity extends FragmentActivity {

	protected ActionBar mActionBar;
	protected NoScrollViewPager mViewPager;
	protected ViewPagerAdapter viewPagerAdapter;
	protected List<Fragment> fragments = new ArrayList<>();
	protected List<String> lstTitles = new ArrayList<>();

	public RFIDWithUHFUART reader;

	public void initUHF() {
		this.mActionBar = getActionBar();
		this.mActionBar.setDisplayShowTitleEnabled(true);
		this.mActionBar.setDisplayShowHomeEnabled(true);
		this.mActionBar.setDisplayHomeAsUpEnabled(true);
		this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		try {
			this.reader = RFIDWithUHFUART.getInstance();
		} catch (Exception ex) {
            this.toastMessage(ex.getMessage());
			return;
		}

		if (this.reader != null) {
			new InitTask(this.reader).execute();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initViewPager() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        this.viewPagerAdapter = new ViewPagerAdapter(fragmentManager, fragments, lstTitles);
		this.mViewPager = findViewById(R.id.pager);
        this.mViewPager.setAdapter(viewPagerAdapter);
		int offscreenPage = 2;
		mViewPager.setOffscreenPageLimit(offscreenPage);
	}

	public void toastMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		private ProgressDialog mypDialog;
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
			this.mypDialog.cancel();
			if (!result) {
				Toast
                    .makeText(BaseTabFragmentActivity.this, "init fail", Toast.LENGTH_SHORT)
                    .show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.mypDialog = new ProgressDialog(BaseTabFragmentActivity.this);
			this.mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.mypDialog.setMessage("init...");
			this.mypDialog.setCanceledOnTouchOutside(false);
			this.mypDialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		if (this.reader != null) {
			this.reader.free();
		}
		super.onDestroy();
	}
}
