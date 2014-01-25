package com.gloria.mygoals;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author glorilla
 *
 */
public class MainActivity extends FragmentActivity {
    static final int NUM_ITEMS = 2; // nb of pages

    MyAdapter mAdapter;	// adapter that provides the page to draw

    ViewPager mPager; // the page renderer
    
    PageIndicator mIndicator; // the page indicator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.list_pager);
        mPager.setAdapter(mAdapter);
        
        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.goal_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		int position = mPager.getCurrentItem();
	    switch (item.getItemId()) {
	        case R.id.action_new:
	        	if (position % NUM_ITEMS == 0) {
	        		// Goal page
	        		openNewGoalActivity();
	    		} else { 
	    			// Event page
	        		// TODO To implement the action bar "new" button for event page
	    		} 
	            return true;
	        case R.id.action_settings:
	            openSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
 
	private void openNewGoalActivity() {
	    Intent intent = new Intent(this, EditGoalActivity.class);
	    startActivity(intent);
	}
	
	private void openSettings() {
		// TODO To implement the "setting" button
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

}