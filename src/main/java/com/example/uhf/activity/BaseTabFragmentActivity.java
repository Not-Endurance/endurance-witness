package com.example.uhf.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
	protected ViewPagerAdapter mViewPagerAdapter;
	protected List<Fragment> fragments = new ArrayList<>();
	protected List<String> lstTitles = new ArrayList<>();

	public RFIDWithUHFUART mReader;

	public void initUHF() {
		mActionBar = getActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		try {
			mReader = RFIDWithUHFUART.getInstance();
		} catch (Exception ex) {

			toastMessage(ex.getMessage());

			return;
		}

		if (mReader != null) {
			new InitTask().execute();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initViewPager() {
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, lstTitles);
		mViewPager = findViewById(R.id.pager);
		mViewPager.setAdapter(mViewPagerAdapter);
		int offscreenPage = 2;
		mViewPager.setOffscreenPageLimit(offscreenPage);
	}

	public void toastMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog mypDialog;

		@Override
		protected Boolean doInBackground(String... params) {
			return mReader.init();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			mypDialog.cancel();

			if (!result) {
				Toast.makeText(BaseTabFragmentActivity.this, "init fail",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mypDialog = new ProgressDialog(BaseTabFragmentActivity.this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("init...");
			mypDialog.setCanceledOnTouchOutside(false);
			mypDialog.show();
		}
	}

	@Override
	protected void onDestroy() {

		if (mReader != null) {
			mReader.free();
		}
		super.onDestroy();
	}
}
