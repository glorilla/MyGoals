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

public class EventListFragment extends Fragment {
	// For log purpose
	private static final  String TAG = "EventListFragment";	
	
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
		root_view = inflater.inflate(R.layout.event_list, container, false);
		
        ListView lv= (ListView)root_view.findViewById(R.id.lv_events); // get the listview in the view hierarchy
        
		ContentResolver cr = getActivity().getContentResolver();
		mCursor = cr.query(MyGoals.Tasks.CONTENT_URI, MyGoalsProvider.TASK_PROJECTION, null, null, null);
		
		String[] from = new String[] {
				MyGoals.Tasks.COLUMN_NAME_DUE_DATE, 
				MyGoals.Tasks.COLUMN_NAME_TITLE,
				MyGoals.Tasks.COLUMN_NAME_GOAL_ID,
				MyGoals.Tasks.COLUMN_NAME_STATUS
				};
	    
		int[] to = new int[] { 
	    		R.id.t_event_date, 
	    		R.id.t_event_title, 
	    		R.id.t_event_goal,
	    		R.id.t_progress
	    		};

	    // fill in the activity list layout
	    mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.event_item, mCursor, from, to, 0);

	    mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				if (view.getId()==R.id.t_event_date){
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
	    
        // Define action when clicking on a Goal item 
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Log.v("DEBUG", "Item "+id+" has been clicked");	
        	}
        });
		
		return root_view;
	}	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    }
    
    @Override    
    public void onStart() {
    	super.onStart();
    	
	    // create the list mapping
		ContentResolver cr = getActivity().getContentResolver();
		mCursor = cr.query(MyGoals.Tasks.CONTENT_URI, MyGoalsProvider.TASK_PROJECTION, null, null, null);

		mAdapter.changeCursor(mCursor);	
	}

	@Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu method");			
        inflater.inflate(R.menu.event_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");			
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_new:
        		openNewEventActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
 
	private void openNewEventActivity() {
		Log.d(TAG,"openNewEventActivity method");				
	    /* TODO to implement the method to create a new task 
	    Intent intent= new Intent(getActivity(), EditGoalActivity.class);
	    intent.putExtra(EditGoalActivity.EXTRA_KEY_MODE, EditGoalActivity.Mode.NEW);
	    startActivity(intent);*/
	}	    
    
    
}
