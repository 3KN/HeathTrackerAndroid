package tracker.health.buzzapps.ua.heathtracker.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

import tracker.health.buzzapps.ua.heathtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    private OnLogoutListener mListener;
    TabLayout tabLayout;
    Toolbar toolbar;
    ViewPager viewPager;
    MyAdapter adapter;


    public MainFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_main, container, false);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        getActivity().getMenuInflater().inflate(R.menu.main, toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(this);
        viewPager = (ViewPager)view.findViewById(R.id.main_viewpager);
        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        adapter = new MyAdapter(this.getChildFragmentManager());
        adapter.addFragment(new ProfileFragment(),"Profile");
        adapter.addFragment(new PulseFragment(),"Pulse");
        adapter.addFragment(new SugarFragment(),"Sugar");
        adapter.addFragment(new FatigueFragment(),"Fatigue");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        return view;

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                mListener.onLogout();
                return true;
        }
        return false;
    }

    public interface OnLogoutListener {
        void onLogout();
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLogoutListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLogoutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

}
