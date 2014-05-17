package com.gloria.mygoals;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

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
        
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        /* The PageIndicator overrides the OnPageChangelistener affectation on the ViewPager.
        But it offers the method setOnPageChangeListener to continue the treatment on these events */
        mIndicator.setOnPageChangeListener(this);
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
    	if (position % NUM_ITEMS == 0) {
    		// Activity title set according to the goal list page
            setTitle(getResources().getText(R.string.my_goals));
		} else { 
    		// Activity title set according to the goal list page
            setTitle(getResources().getText(R.string.my_tasks));
		} 		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

}