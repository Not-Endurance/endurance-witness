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

public class FragmentBase extends FragmentActivity {

	protected ActionBar actionBar;
	protected NoScrollViewPager viewPager;
	protected ViewPagerAdapter viewPagerAdapter;
	protected List<Fragment> fragments = new ArrayList<>();
	protected List<String> lstTitles = new ArrayList<>();

	public RFIDWithUHFUART reader;

	public void initUHF() {
		this.actionBar = getActionBar();
		this.actionBar.setDisplayShowTitleEnabled(true);
		this.actionBar.setDisplayShowHomeEnabled(true);
		this.actionBar.setDisplayHomeAsUpEnabled(true);
		this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
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
		this.viewPager = findViewById(R.id.pager);
        this.viewPager.setAdapter(viewPagerAdapter);
		int offscreenPage = 2;
		viewPager.setOffscreenPageLimit(offscreenPage);
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
                    .makeText(FragmentBase.this, "init fail", Toast.LENGTH_SHORT)
                    .show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.mypDialog = new ProgressDialog(FragmentBase.this);
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
