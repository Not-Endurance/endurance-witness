package com.example.uhf.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.example.uhf.R;
import com.example.uhf.adapter.ViewPagerAdapter;
import com.example.uhf.fragment.KeyDwonFragment;
import com.example.uhf.widget.NoScrollViewPager;
import com.rscja.deviceapi.RFIDWithUHFUART;

public class BaseTabFragmentActivity extends FragmentActivity {

	protected ActionBar mActionBar;
	protected NoScrollViewPager mViewPager;
	protected ViewPagerAdapter mViewPagerAdapter;
	protected List<KeyDwonFragment> lstFrg = new ArrayList<>();
	protected List<String> lstTitles = new ArrayList<>();

	public RFIDWithUHFUART mReader;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

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

	protected void initViewPageData() {	}

	protected void initViewPager() {
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), lstFrg, lstTitles);
		mViewPager = findViewById(R.id.pager);
		mViewPager.setAdapter(mViewPagerAdapter);
		int offscreenPage = 2;
		mViewPager.setOffscreenPageLimit(offscreenPage);
	}

	protected void initTabs() {
		for (int i = 0; i < mViewPagerAdapter.getCount() - 3; ++i) {
			mActionBar.addTab(mActionBar.newTab()
					.setText(mViewPagerAdapter.getPageTitle(i)).setTabListener(mTabListener));
		}
	}

	protected ActionBar.TabListener mTabListener = new ActionBar.TabListener() {

		@Override
		public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
			if (mActionBar.getTabCount() > 3 && tab.getPosition() != 3) {
				mActionBar.removeTabAt(3);
			}
			if (tab.getPosition() == 3) {
				int index = 0;
				mViewPager.setCurrentItem(index, false);
			} else {
				mViewPager.setCurrentItem(tab.getPosition());
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 139 ||keyCode == 280 ||keyCode == 293) {

			if (event.getRepeatCount() == 0) {

				if (mViewPager != null) {

					KeyDwonFragment sf = (KeyDwonFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
					sf.myOnKeyDwon();

				}
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
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
