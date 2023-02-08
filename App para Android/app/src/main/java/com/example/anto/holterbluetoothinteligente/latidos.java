package com.example.anto.holterbluetoothinteligente;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class latidos extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_latidos, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.vpPager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));

        return v;
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;

            switch (position) {
                case 0:
                    fragment = new latidos_f1();
                    return fragment;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Total";
                case 1:
                    return "Ventriculares";
                case 2:
                    return "Supraventriculares";
                default:
                    return null;

            }
        }
    }
}