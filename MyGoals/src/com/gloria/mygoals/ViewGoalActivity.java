/**
 * 
 */
package com.gloria.mygoals;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * @author glorilla
 *
 */
public class ViewGoalActivity extends FragmentActivity implements OnPageChangeListener {
    static final int NUM_ITEMS = 2; // nb of pages

    MyAdapter mAdapter;	// adapter that provides the page to draw

    ViewPager mPager; // the page renderer
    
    PageIndicator mIndicator; // the page indicator    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_page);
        
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.goal_pager);
        mPager.setAdapter(mAdapter);
        
        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        /* The PageIndicator overrides the OnPageChangelistener affectation on the ViewPager.
        But it offers the method setOnPageChangeListener to continue the treatment on these events */
        mIndicator.setOnPageChangeListener(this);
        
		// TODO Set the goal title as activity title
        setTitle("To Pass the PMP Exam");
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
				return new GoalDetailFragment();				
			} else { 
				return new ActivityListFragment();
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
    		// TODO Set the goal title as activity title
            setTitle("To Pass the PMP Exam");
		} else { 
    		// TODO To use @string references
            setTitle("Associated activities");
		} 	
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}