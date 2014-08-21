package com.gloria.mygoals;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import static android.app.ActionBar.TabListener;

/**
 * @author glorilla
 *
 */
public class MainActivity extends FragmentActivity implements OnPageChangeListener {
	// For log purpose
	private static final String TAG = "GoalDetailFragment";
	
    static final int NUM_ITEMS = 2; // nb of pages

    ViewPager mViewPager; // the pages container
    MyAdapter mAdapter;	// adapter that provides the fragment to set in the viewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate method");		
		
        setContentView(R.layout.main);
        this.setTitle("My Goals");
        
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager)findViewById(R.id.list_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);

        // set the action bar tabs
        initActionBarTabs();
    }
	
    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
        	if (position % NUM_ITEMS == 0) {
				return new GoalListFragment();
			} else { 
        		return new EventListFragment();
			} 
        }
    }

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

    private void initActionBarTabs() {
        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        TabListener tabListener = new TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
                // Do nothing
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
                // Do nothing
            }
        };

        // Add 2 tabs, specifying the tab's text and TabListener
        actionBar.addTab(actionBar.newTab()
                        .setText(getResources().getText(R.string.my_goals))
                        .setTabListener(tabListener)
        );
        actionBar.addTab(actionBar.newTab()
                        .setText(getResources().getText(R.string.my_tasks))
                        .setTabListener(tabListener)
        );

    }

}