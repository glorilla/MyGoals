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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author glorilla
 *
 */
public class MainActivity extends FragmentActivity implements OnPageChangeListener {
	// For log purpose
	private static final  String TAG = "GoalDetailFragment"; 
	
    static final int NUM_ITEMS = 2; // nb of pages

    MyAdapter mAdapter;	// adapter that provides the page to draw

    ViewPager mPager; // the page renderer
    
    PageIndicator mIndicator; // the page indicator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate method");		
		
        setContentView(R.layout.main);
        this.setTitle("My Goals");
        
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.list_pager);
        mPager.setAdapter(mAdapter);
        
        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        /* The PageIndicator overrides the OnPageChangelistener affectation on the ViewPager.
        But it offers the method setOnPageChangeListener to continue the treatment on these events */
        mIndicator.setOnPageChangeListener(this);
    }
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,"onCreateOptionsMenu method");				
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.goal_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");			
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
		Log.d(TAG,"openNewGoalActivity method");				
	    Intent intent = new Intent(this, EditGoalActivity.class);
	    intent.putExtra(EditGoalActivity.EXTRA_KEY_MODE, EditGoalActivity.Mode.NEW);
	    startActivity(intent);
	}
	
	private void openSettings() {
		Log.d(TAG,"openSettings method");				
		// TODO To implement the "setting" button
	}
*/	
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
    	if (position % NUM_ITEMS == 0) {
    		// Activity title set according to the goal list page
            setTitle("My Goals");
		} else { 
    		// Activity title set according to the goal list page
            setTitle("My Tasks");
		} 		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

}