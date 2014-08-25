package com.gloria.mygoals;

import android.app.DialogFragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

;

public class GoalDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	// For log purpose
	private static final  String TAG = "GoalDetailFragment"; 

	// Views
	private View mRootView;
	private TextView mVTitle;
	private TextView mVDescription;
	private TextView mVStartDate;
	private TextView mVEndDate;
	private TextView mVWorkload;

	// Values
	private Date mStartDate;
	private Date mEndDate;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate method");			
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);        
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView method");					
		// Return the View built from the layout
		mRootView = inflater.inflate(R.layout.goal_view, container, false);

		mVTitle=((TextView)mRootView.findViewById(R.id.t_goal_detail_title));
		mVDescription=((TextView)mRootView.findViewById(R.id.t_goal_detail_description));
		mVStartDate=((TextView)mRootView.findViewById(R.id.t_goal_start_date));
		mVEndDate=((TextView)mRootView.findViewById(R.id.t_goal_end_date));
		mVWorkload=((TextView)mRootView.findViewById(R.id.t_goal_hours));

		return mRootView;
	}	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG,"onActivityCreated method");	      	
    	super.onActivityCreated(savedInstanceState);
  	}

    @Override
    public void onStart() {
		Log.d(TAG,"onStart method");
		super.onStart();

        getLoaderManager().initLoader(0, null, this);
    }
    
	@Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu method");			
        inflater.inflate(R.menu.goal_detail_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");			
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_edit:
                openEditGoalDialog();
            default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private void openEditGoalDialog() {
        Log.d(TAG, "openEditGoalDialog method");
        DialogFragment dialog = new EditGoalDialog(R.string.modify_goal, EditGoalDialog.Mode.EDIT, ViewGoalActivity.mGoalId);
        dialog.show(getActivity().getFragmentManager(), "EditGoalDialog");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG,"onCreateLoader");
        Uri uri = Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + ViewGoalActivity.mGoalId);

        return new CursorLoader(getActivity(), uri, MyGoalsProvider.GOAL_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG,"onLoadFinished");
        swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG,"onLoaderReset");
    }

    private void swapCursor(Cursor cursor) {
        if(null==cursor || cursor.getCount()<=0) return;

        // if cursor is not empty, get the values
        cursor.moveToFirst();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
        mVTitle.setText(cursor.getString(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_TITLE)));
        mVDescription.setText(cursor.getString(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_DESC)));

        int duration = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_WORKLOAD)));
        String dur = DateUtils.formatElapsedTime(duration);
        if (duration<3600) {
            mVWorkload.setText(dur.substring(0, dur.length() - 3) + " " + getResources().getString(R.string.minutes));
        } else {
            mVWorkload.setText(dur.substring(0, dur.length() - 3) + " " + getResources().getString(R.string.hours));
        }

        try {
            mStartDate = sdf.parse(cursor.getString(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_START_DATE)));
            mEndDate = sdf.parse(cursor.getString(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_TARGET_DATE)));
            mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
            mVEndDate.setText(SimpleDateFormat.getDateInstance().format(mEndDate));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
