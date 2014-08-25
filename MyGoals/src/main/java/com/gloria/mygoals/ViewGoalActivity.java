/**
 *
 */
package com.gloria.mygoals;

import android.app.ActionBar;
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

/**
 * @author glorilla
 */
public class ViewGoalActivity extends FragmentActivity implements OnPageChangeListener {
    public static final String EXTRA_KEY_ID = "id";
    public static final String EXTRA_KEY_TITLE = "title";
    public static final String EXTRA_KEY_RESULT = "result";
    static final int NUM_ITEMS = 2; // nb of pages
    public static int mGoalId;        // the id of the displayed Goal
    public static String mGoalTitle;    // the title of the displayed Goal
    // For log purpose
    private final String TAG = "ViewGoalActivity";
    ViewPager mViewPager; // the page renderer
    MyAdapter mAdapter;    // adapter that provides the page to draw

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate method");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_page);

        // Set the action bar
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.goal_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);

        // Set the goal title as activity title
        mGoalTitle = getIntent().getStringExtra(EXTRA_KEY_TITLE);
        setTitle(mGoalTitle);

        // get the Goal id
        mGoalId = getIntent().getIntExtra(ViewGoalActivity.EXTRA_KEY_ID, 0);

        // set the action bar tabs
        initActionBarTabs();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected method");
        getActionBar().setSelectedNavigationItem(position);
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
        Log.d(TAG, "onOptionsItemSelected method");
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
        Log.d(TAG, "removeGoal method");
        new AlertDialog.Builder(this)
                .setTitle(getResources().getText(R.string.dialog_title_deleteGoal))
                .setMessage(getResources().getText(R.string.dialog_message_deleteGoal))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ContentResolver cr = getContentResolver();
                        int nbRow = cr.delete(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + mGoalId), null, null);
                        Log.d(TAG, "" + nbRow + " rows of the goal table have been successfully deleted");

                        nbRow = cr.delete(MyGoals.Activities.CONTENT_URI, MyGoals.Activities.SELECT_BY_GOAL_ID, new String[]{"" + mGoalId});
                        Log.d(TAG, "" + nbRow + " rows of the activity table have been successfully deleted");

                        nbRow = cr.delete(MyGoals.Tasks.CONTENT_URI, MyGoals.Tasks.SELECT_BY_GOAL_ID, new String[]{"" + mGoalId});
                        Log.d(TAG, "" + nbRow + " rows of the task table have been successfully deleted");

                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_KEY_RESULT, result.DELETION);
                        setResult(RESULT_OK, getIntent());
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

    private void initActionBarTabs() {
        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
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
                        .setText(getResources().getText(R.string.my_goal))
                        .setTabListener(tabListener)
        );
        actionBar.addTab(actionBar.newTab()
                        .setText(getResources().getText(R.string.activities))
                        .setTabListener(tabListener)
        );

    }

    public static enum result {DELETION}

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
}
