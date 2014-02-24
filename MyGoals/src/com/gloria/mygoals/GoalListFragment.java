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
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GoalListFragment extends Fragment {
	// For log purpose
	private static final  String TAG = "GoalListFragment";
	
	private View mRootView;
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
		mInflater = inflater;
		mRootView = inflater.inflate(R.layout.goal_list, container, false);
		// get the listview in the view hierarchy
		ListView lv= (ListView)mRootView.findViewById(R.id.lv_goals);
    	
	    // create the list mapping
		ContentResolver cr = getActivity().getContentResolver();
		mCursor = cr.query( MyGoals.Goals.CONTENT_URI, MyGoalsProvider.GOAL_PROJECTION, null, null, null);
		
		String[] from = new String[] {
				MyGoals.Goals.COLUMN_NAME_TITLE, 
				MyGoals.Goals.COLUMN_NAME_DESC,
				MyGoals.Goals.COLUMN_NAME_TARGET_DATE,
				MyGoals.Goals.COLUMN_NAME_PROGRESS
				};
	    
		int[] to = new int[] { 
	    		R.id.t_goal_title, 
	    		R.id.t_goal_desc, 
	    		R.id.t_goal_end_date,
	    		R.id.goal_progress
	    		};
		

	    // fill in the goal list layout
	    //TODO Create a custom adapter to display Goal in the list of goals with category icon, colors ...

	    mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.goal_item, mCursor, from, to, 0);
 
	    mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				// TODO Auto-generated method stub
				if (view.getId()==R.id.t_goal_end_date){
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
				if (view.getId()==R.id.goal_progress) {
		        	int value=Integer.parseInt((String)cursor.getString(columnIndex));
		        	((ProgressBar)view).setProgress(value);
		        	return true;
		        }
				return false;
			}
		});
	    
	    // Add a footer to the list to add new goals
	    View footer = mInflater.inflate(R.layout.footer_goal, null);
	    lv.addFooterView(footer);
	    footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
        		openNewGoalActivity();
			}
		});
    
	    // Define action when clicking on a Goal item 
	    lv.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("DEBUG", "Goal "+id+" has been clicked");
				mCursor.moveToPosition(position);
				int goal_id = mCursor.getInt(MyGoalsProvider.GOAL_ID_INDEX);
				viewGoal(mCursor, goal_id);
	    	}
	    });
	    
	    lv.setAdapter(mAdapter);		
		return mRootView;
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
		mCursor = cr.query( MyGoals.Goals.CONTENT_URI, MyGoalsProvider.GOAL_PROJECTION, null, null, null);

		mAdapter.changeCursor(mCursor);
    }
    
	private void viewGoal(Cursor c, int goal_id) {
	    Intent intent = new Intent(getActivity(), ViewGoalActivity.class);
	    
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_ID, goal_id);
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_TITLE, c.getString(MyGoalsProvider.GOAL_TITLE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_DESC, c.getString(MyGoalsProvider.GOAL_DESC_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_START_DATE, c.getString(MyGoalsProvider.GOAL_START_DATE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_TARGET_DATE, c.getString(MyGoalsProvider.GOAL_TARGET_DATE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_WORKLOAD, c.getInt(MyGoalsProvider.GOAL_WORKLOAD_INDEX));
	    
	    startActivityForResult(intent, 0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			ViewGoalActivity.result res = (ViewGoalActivity.result)data.getExtras().get(ViewGoalActivity.EXTRA_KEY_RESULT);
			switch (res) {
			case DELETION:
				Toast.makeText(getActivity(), "Goal deleted", Toast.LENGTH_SHORT).show();
				break;
			default:
			}
		}
	}
    
	@Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu method");			
        inflater.inflate(R.menu.goal_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");			
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_new:
        		openNewGoalActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
 
	private void openNewGoalActivity() {
		Log.d(TAG,"openNewGoalActivity method");				
	    Intent intent= new Intent(getActivity(), EditGoalActivity.class);
	    intent.putExtra(EditGoalActivity.EXTRA_KEY_MODE, EditGoalActivity.Mode.NEW);
	    startActivity(intent);
	}	
	
}
