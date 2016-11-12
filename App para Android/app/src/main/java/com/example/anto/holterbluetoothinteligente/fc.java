package com.example.anto.holterbluetoothinteligente;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class fc extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fc, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.vpPager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));

        return v;
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

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
                    fragment = new fc1();
                    return fragment;
                case 1:
                    fragment = new fc2();
                    return fragment;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Estadísticas";
                case 1:
                    return "Gráfico";
                default:
                    return null;

            }
        }
    }
}