package ch.hevs.aislab.paams.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paamsdemo.R;


public class PhysioParamFragment extends Fragment {

    private static final String TAG = "PhysioParamFragment";

    private static final String ARG_TYPE = "TYPE_OF_PHYSIOLOGICAL_VALUE";

    private Type type;
    private AppBarLayout appBar;
    private TabLayout tabs;
    private ViewPager viewPager;

    public PhysioParamFragment() {

    }

    public static PhysioParamFragment newInstance(Type type) {
        PhysioParamFragment fragment = new PhysioParamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = Type.valueOf(getArguments().getString(ARG_TYPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paged, container, false);

        Log.i(TAG, "onCreateView() from " + type);

        insertTabs(container);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        populateViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView() from " + type);
        super.onDestroyView();
        if (appBar != null) {
            appBar.removeView(tabs);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState() from " + type);
        super.onSaveInstanceState(outState);
    }

    private void insertTabs(ViewGroup container) {
        View rootView = (View) container.getParent();
        appBar = (AppBarLayout) rootView.findViewById(R.id.appbar);
        tabs = new TabLayout(getActivity());
        tabs.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        appBar.addView(tabs);
    }

    private void populateViewPager(ViewPager viewPager) {
        SectionsAdapter adapter = new SectionsAdapter(getFragmentManager());
        adapter.addFragment(ListValuesFragment.newInstance(type), "Past Values");
        adapter.addFragment(AddValueFragment.newInstance(type), "New Value");
        viewPager.setAdapter(adapter);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public class SectionsAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        SectionsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
