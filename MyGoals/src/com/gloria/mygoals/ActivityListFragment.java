package com.gloria.mygoals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.gloria.mygoals.dummy.DummyData;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ActivityListFragment extends Fragment {
	// For log purpose
	private static final  String TAG = "ActivityListFragment";
	
	private View root_view;
	private LayoutInflater mInflater;
	private SimpleCursorAdapter mAdapter;
	private Cursor mCursor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);        
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Return the View built from the layout
		root_view = inflater.inflate(R.layout.activity_list, container, false);
		
		 // get the listview in the view hierarchy
		ListView lv= (ListView)root_view.findViewById(R.id.lv_activities);
	    
		ContentResolver cr = getActivity().getContentResolver();
		
		//mGoalId=getActivity().getIntent().getIntExtra(ViewGoalActivity.EXTRA_KEY_ID, 0);
		
		Uri uri = Uri.parse(MyGoals.Activities.CONTENT_URI + "?goal_id=" + ViewGoalActivity.mGoalId);
		mCursor = cr.query(uri, MyGoalsProvider.ACTIVITY_PROJECTION, null, null, null);
		
		String[] from = new String[] {
				MyGoals.Activities.COLUMN_NAME_TITLE, 
				MyGoals.Activities.COLUMN_NAME_START_DATE,
				MyGoals.Activities.COLUMN_NAME_END_DATE,
				MyGoals.Activities.COLUMN_NAME_REPETITION,
				MyGoals.Activities.COLUMN_NAME_DURATION,
				MyGoals.Activities.COLUMN_NAME_NB_TASKS
				};
	    
		int[] to = new int[] { 
	    		R.id.t_activity_title, 
	    		R.id.t_start_date, 
	    		R.id.t_end_date,
	    		R.id.t_status,
	    		R.id.t_progress, 
	    		R.id.t_effort
	    		};

	    // fill in the activity list layout
	    mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.activity_item, mCursor, from, to, 0);

	    mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				if (view.getId()==R.id.t_start_date || view.getId()==R.id.t_end_date){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);

					try {
						Date endDate = sdf.parse(cursor.getString(columnIndex));
						((TextView)view).setText(SimpleDateFormat.getDateInstance().format(endDate));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				
				// TODO Auto-generated method stub
				
				return false;
			}
	    });
	    
	    lv.setAdapter(mAdapter);

	    // Define action when clicking on an Activity item 
	    lv.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.v("DEBUG", "Activity "+id+" has been clicked");	
	    	}
	    });
		
		return root_view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	if (root_view != null) {

    	}
    }
    
    @Override    
    public void onStart() {
    	super.onStart();

	    // create the list mapping
		ContentResolver cr = getActivity().getContentResolver();
		Uri uri = Uri.parse(MyGoals.Activities.CONTENT_URI + "?goal_id=" +  ViewGoalActivity.mGoalId);
		mCursor = cr.query(uri, MyGoalsProvider.ACTIVITY_PROJECTION, null, null, null);
		
		mAdapter.changeCursor(mCursor);
	}
    
	@Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu method");			
        inflater.inflate(R.menu.activity_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");			
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_new:
        		openNewActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void openNewActivity() {
		Log.d(TAG,"openEditActivity method");				
	    Intent intent = new Intent(getActivity(), EditActivity.class);

	    intent.putExtra(EditActivity.EXTRA_KEY_MODE, EditActivity.Mode.NEW);
  	    intent.putExtra(EditActivity.EXTRA_KEY_GOAL_ID, ViewGoalActivity.mGoalId);
  	    intent.putExtra(EditActivity.EXTRA_KEY_GOAL_TITLE, ViewGoalActivity.mGoalTitle);  	    
  
	    startActivity(intent);
	}		
	
}
