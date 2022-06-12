package endurance.witness.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> lstTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.lstTitles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        if (this.fragments.size() > 0) {
            return this.fragments.get(position);
        }
        throw new IllegalStateException("No fragment at position " + position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (this.lstTitles.size() > 0) {
            return this.lstTitles.get(position);
        }
        return null;
    }
}
