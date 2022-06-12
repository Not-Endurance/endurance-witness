package endurance.witness.android.fragments;

import endurance.witness.android.adapters.ViewPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import java.util.ArrayList;
import java.util.List;
import com.example.Android.R;
import endurance.witness.android.widgets.NoScrollViewPager;

public class FragmentBase extends FragmentActivity {
	protected NoScrollViewPager viewPager;
	protected ViewPagerAdapter viewPagerAdapter;
	protected List<Fragment> fragments = new ArrayList<>();
	protected List<String> lstTitles = new ArrayList<>();

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

}
