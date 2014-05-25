package com.gloria.mygoals;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GoalListFragment extends Fragment implements /*Refreshable,*/ LoaderManager.LoaderCallbacks<Cursor> {
	// For log purpose
	private static final  String TAG = "GoalListFragment";
	
	private View mRootView;
	private LayoutInflater mInflater;
	private SimpleCursorAdapter mAdapter;
	//private Cursor mCursor;
	
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
		/*ContentResolver cr = getActivity().getContentResolver();
		mCursor = cr.query(MyGoals.Goals.CONTENT_URI, MyGoalsProvider.GOAL_PROJECTION, null, null, null);*/
		
		String[] from = new String[] {
				MyGoals.Goals.COLUMN_NAME_TITLE, 
				MyGoals.Goals.COLUMN_NAME_DESC,
				MyGoals.Goals.COLUMN_NAME_TARGET_DATE,
				MyGoals.Goals.COLUMN_NAME_PROGRESS,
                MyGoals.Goals.COLUMN_NAME_COLOR
				};
	    
		int[] to = new int[] { 
	    		R.id.t_goal_title, 
	    		R.id.t_goal_desc, 
	    		R.id.t_goal_end_date,
	    		R.id.goal_progress,
                R.id.root_layout
	    		};
		

	    // fill in the goal list layout
	    //TODO Create a custom adapter to display Goal in the list of goals with category icon, colors ...

	    mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.goal_item, /*mCursor*/ null, from, to, 0);
 
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
		        	int value=cursor.getInt(columnIndex);
		        	int max=cursor.getInt(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_WORKLOAD));
		        	((ProgressBar)view).setProgress(value);
		        	((ProgressBar)view).setMax(max);
		        	return true;
		        }
                if (view.getId()==R.id.root_layout) {
                    int value=cursor.getInt(columnIndex);
                    view.setBackgroundColor(value);
                    return true;
                }
				return false;
			}
		});
	    
	    // Define action when clicking on a Goal item 
	    lv.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("DEBUG", "Goal "+id+" has been clicked");
				Cursor cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);
				int goal_id = cursor.getInt(MyGoalsProvider.GOAL_ID_INDEX);
				viewGoal(cursor, goal_id);
	    	}
	    });
	    
	    // Add a footer to the list to add new goals
	    View footer = mInflater.inflate(R.layout.footer, null);
	    TextView footerText=((TextView)footer.findViewById(R.id.t_footer_text));
	    footerText.setHint(R.string.footer_add_goal);
	    footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
        		openNewGoalActivity();
			}
		});	    

	    lv.addFooterView(footer);
	    
	    lv.setAdapter(mAdapter);
	    
		return mRootView;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }
	
    @Override
    public void onResume() {
	    // create the list mapping
    	Log.d(TAG,"onResume");
		/*
    	if (null != mRootView) {
			ContentResolver cr = getActivity().getContentResolver();
			
			mCursor.close();
			mCursor = cr.query( MyGoals.Goals.CONTENT_URI, MyGoalsProvider.GOAL_PROJECTION, null, null, null);

			mAdapter.changeCursor(mCursor);
		}*/
    	
    	super.onResume();
    }

    @Override
    public void onDestroyView() {
    	//mCursor.close();
    	super.onDestroyView();
    }	    
    
	private void viewGoal(Cursor c, int goal_id) {
	    Intent intent = new Intent(getActivity(), ViewGoalActivity.class);
	    
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_ID, goal_id);
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_TITLE, c.getString(MyGoalsProvider.GOAL_TITLE_INDEX));
	    /*
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_DESC, c.getString(MyGoalsProvider.GOAL_DESC_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_START_DATE, c.getString(MyGoalsProvider.GOAL_START_DATE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_TARGET_DATE, c.getString(MyGoalsProvider.GOAL_TARGET_DATE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_WORKLOAD, c.getInt(MyGoalsProvider.GOAL_WORKLOAD_INDEX));
	    */
	    startActivityForResult(intent, 0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			ViewGoalActivity.result res = (ViewGoalActivity.result)data.getExtras().get(ViewGoalActivity.EXTRA_KEY_RESULT);
			switch (res) {
			case DELETION:
                Toast.makeText(getActivity(), getResources().getText(R.string.Goal_deleted), Toast.LENGTH_SHORT).show();
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

	/*@Override
	public void OnRefresh() {
	   	Log.d(TAG,"OnRefresh");
		if (null != mRootView) {
			ContentResolver cr = getActivity().getContentResolver();
			
			mCursor.close();
			mCursor = cr.query( MyGoals.Goals.CONTENT_URI, MyGoalsProvider.GOAL_PROJECTION, null, null, null);

			mAdapter.changeCursor(mCursor);
		}
	}*/

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG,"onCreateLoader");
        return new CursorLoader(getActivity(), MyGoals.Goals.CONTENT_URI, MyGoalsProvider.GOAL_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG,"onLoadFinished");
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG,"onLoaderReset");
        mAdapter.swapCursor(null);
    }
}
