/**
 * 
 */
package com.gloria.mygoals;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
 * @author glorilla
 *
 */
public class ViewGoalActivity extends FragmentActivity implements OnPageChangeListener {
	// For log purpose
	private final String TAG = "ViewGoalActivity"; 
	
	static final int NUM_ITEMS = 2; // nb of pages

	public static final String EXTRA_KEY_ID = "id";
	public static final String EXTRA_KEY_TITLE = "title";
	/*public static final String EXTRA_KEY_DESC = "desc";
	public static final String EXTRA_KEY_START_DATE = "start_date";
	public static final String EXTRA_KEY_TARGET_DATE = "target_date";
	public static final String EXTRA_KEY_WORKLOAD = "workload";*/
	
	public static final String EXTRA_KEY_RESULT = "result";
	public static enum result {DELETION}
	
    MyAdapter mAdapter;	// adapter that provides the page to draw

    ViewPager mPager; // the page renderer
    
    PageIndicator mIndicator; // the page indicator    
    
	public static int mGoalId; 		// the id of the displayed Goal
	public static String mGoalTitle;	// the title of the displayed Goal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate method");		    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_page);

		// Set the action bar
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    getActionBar().setDisplayHomeAsUpEnabled(true);
        }          
        
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.goal_pager);
        mPager.setAdapter(mAdapter);
        
        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        /* The PageIndicator overrides the OnPageChangelistener affectation on the ViewPager.
        But it offers the method setOnPageChangeListener to continue the treatment on these events */
        mIndicator.setOnPageChangeListener(this);
 
        // Set the goal title as activity title
        mGoalTitle = getIntent().getStringExtra(EXTRA_KEY_TITLE);
        setTitle(mGoalTitle);

        // get the Goal id
    	mGoalId=getIntent().getIntExtra(ViewGoalActivity.EXTRA_KEY_ID, 0);
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
		Log.d(TAG,"onPageSelected method");				
    	if (position % NUM_ITEMS == 0) {
    		// Set the goal title as activity title
            setTitle(getIntent().getStringExtra(EXTRA_KEY_TITLE));
		} else { 
    		// Set activities fragment title
            setTitle(R.string.activities);
		} 	
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.goal_view, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");					
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        //TODO proper up navigation : NavUtils.navigateUpFromSameTask(this);
		        finish();
		        return true;
	        case R.id.action_delete:
	        	removeGoal();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
 
	private void removeGoal() {
		Log.d(TAG,"removeGoal method");			
		new AlertDialog.Builder(this)
	    .setTitle("Delete entry")
	    .setMessage("Are you sure you want to delete this goal and all its tasks and activities?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	ContentResolver cr = getContentResolver();
	        	int nbRow =  cr.delete(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + mGoalId), null, null);
	        	Log.d(TAG, "" + nbRow + " rows of the goal table have been successfully deleted");

                nbRow = cr.delete(MyGoals.Activities.CONTENT_URI, MyGoals.Activities.SELECT_BY_GOAL_ID, new String[] {""+mGoalId});
                Log.d(TAG, "" + nbRow + " rows of the activity table have been successfully deleted");

                nbRow = cr.delete(MyGoals.Tasks.CONTENT_URI, MyGoals.Tasks.SELECT_BY_GOAL_ID, new String[] {""+mGoalId});
                Log.d(TAG, "" + nbRow + " rows of the task table have been successfully deleted");

	        	Intent intent = getIntent();
	        	intent.putExtra(EXTRA_KEY_RESULT, result.DELETION);
	        	setResult(RESULT_OK,getIntent());
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .setIcon(R.drawable.alert_warning)
	    .show();
	}

}