/**
 * 
 */
package com.gloria.mygoals;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
public class ViewGoalActivity extends FragmentActivity implements OnPageChangeListener {
    
	private final String TAG = "ViewGoalActivity"; 
	
	static final int NUM_ITEMS = 2; // nb of pages

	public static final String EXTRA_KEY_ID = "id";
	public static final String EXTRA_KEY_TITLE = "title";
	public static final String EXTRA_KEY_DESC = "desc";
	public static final String EXTRA_KEY_START_DATE = "start_date";
	public static final String EXTRA_KEY_TARGET_DATE = "target_date";
	public static final String EXTRA_KEY_WORKLOAD = "workload";
	
	public static final String EXTRA_KEY_RESULT = "result";
	public static enum result {DELETION}
	
    MyAdapter mAdapter;	// adapter that provides the page to draw

    ViewPager mPager; // the page renderer
    
    PageIndicator mIndicator; // the page indicator    
    
	private int mGoalId; // the displayed Goal id

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
        
		// Set the goal title as activity title
        setTitle(getIntent().getStringExtra(EXTRA_KEY_TITLE));
        
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
		getMenuInflater().inflate(R.menu.goal_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		int position = mPager.getCurrentItem();
	    switch (item.getItemId()) {
	        case R.id.action_edit:
	        	if (position % NUM_ITEMS == 0) {
	        		// TODO To implement the action bar "edit" button for the goal fragment
	        		openEditGoalActivity();
	    		} else { 
	        		// TODO To implement the action bar "edit" button for the activity fragment
	    			openEditActivity();
	    		} 
	            return true;
	        case R.id.action_delete:
	        	removeGoal();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void openEditGoalActivity() {
	    Intent intent = new Intent(this, EditGoalActivity.class);

	    intent.putExtra(EditGoalActivity.EXTRA_KEY_MODE, EditGoalActivity.Mode.EDIT);
  	    intent.putExtra(EditGoalActivity.EXTRA_KEY_ID, mGoalId);
  
	    startActivity(intent);
	}

	private void openEditActivity() {
	    Intent intent = new Intent(this, EditActivity.class);

	    intent.putExtra(EditGoalActivity.EXTRA_KEY_MODE, EditGoalActivity.Mode.EDIT);
  	    intent.putExtra(EditGoalActivity.EXTRA_KEY_ID, mGoalId);
  
	    startActivity(intent);
	}	
	
	private void removeGoal() {
		new AlertDialog.Builder(this)
	    .setTitle("Delete entry")
	    .setMessage("Are you sure you want to delete this entry?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	ContentResolver cr = getContentResolver();
	        	int nbRow =  cr.delete(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + mGoalId), null, null);
	        	Log.d(TAG, "" + nbRow + " rows have been successfully deleted");
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