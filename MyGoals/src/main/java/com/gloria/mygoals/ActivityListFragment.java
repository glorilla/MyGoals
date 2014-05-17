package com.gloria.mygoals;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import be.billington.calendar.recurrencepicker.EventRecurrence;
import be.billington.calendar.recurrencepicker.EventRecurrenceFormatter;

public class ActivityListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	// For log purpose
	private static final  String TAG = "ActivityListFragment";
	
	private final String[] activityState = {"Planned", "On Track", "Completed", "Late", "In Advance"};
	
	private View root_view;
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
		mInflater=inflater;
		root_view = inflater.inflate(R.layout.activity_list, container, false);
		
		 // get the listview in the view hierarchy
		ListView lv= (ListView)root_view.findViewById(R.id.lv_activities);
	    
		//ContentResolver cr = getActivity().getContentResolver();
		
		//mGoalId=getActivity().getIntent().getIntExtra(ViewGoalActivity.EXTRA_KEY_ID, 0);
		
		//Uri uri = Uri.parse(MyGoals.Activities.CONTENT_URI + "?goal_id=" + ViewGoalActivity.mGoalId);
		//Cursor = cr.query(uri, MyGoalsProvider.ACTIVITY_PROJECTION, null, null, null);
		
		String[] from = new String[] {
				MyGoals.Activities.COLUMN_NAME_TITLE, 
				MyGoals.Activities.COLUMN_NAME_START_DATE,
				MyGoals.Activities.COLUMN_NAME_END_DATE,
				MyGoals.Activities.COLUMN_NAME_REPETITION,
				MyGoals.Activities.COLUMN_NAME_DURATION,
				MyGoals.Activities.COLUMN_NAME_NB_TASKS,
				MyGoals.Activities.COLUMN_NAME_PROGRESS,
                MyGoals.Activities.COLUMN_NAME_RRULE
				};
	    
		int[] to = new int[] { 
	    		R.id.t_activity_title, 
	    		R.id.t_start_date, 
	    		R.id.t_end_date,
	    		R.id.t_status,
	    		R.id.t_task_duration,	    		
	    		R.id.t_nb_task,
	    		R.id.progress,
                R.id.t_recurrence_rule
	    		};

	    // fill in the activity list layout
	    mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.activity_item, null, from, to, 0);

	    mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				if (view.getId()==R.id.t_start_date || view.getId()==R.id.t_end_date){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);

					try {
						Date date = sdf.parse(cursor.getString(columnIndex));
						((TextView)view).setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(date));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				if (view.getId()==R.id.t_status) {
					((TextView)view).setText(activityState[0]); //Integer.parseInt(cursor.getString(columnIndex))]);
					return true;
				}
				if (view.getId()==R.id.t_task_duration) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
					
					try {
						Date time = sdf.parse(cursor.getString(columnIndex));
						((TextView)view).setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(time)+getString(R.string.hours));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				if (view.getId()==R.id.t_nb_task) {
					int nbTask;
					try {
						nbTask = Integer.parseInt(cursor.getString(columnIndex));
					}
					catch(Exception e) {
						nbTask = 0;
					}
					// it displays the number of tasks only it is greater than 1 
					if (nbTask > 1) {
						((TextView)view).setText(cursor.getString(columnIndex)+" x ");
					}
					return true;
				}				
				if (view.getId()==R.id.progress) {
					((ProgressBar)view).setMax(cursor.getInt(cursor.getColumnIndex(MyGoals.Activities.COLUMN_NAME_NB_TASKS)));
					((ProgressBar)view).setProgress(cursor.getInt(columnIndex));
					return true;
				}
                if (view.getId()==R.id.t_recurrence_rule) {
                    try {
                        EventRecurrence rec = new EventRecurrence();
                        rec.parse(cursor.getString(columnIndex));
                        //rec.setStartDate();

                        String str = EventRecurrenceFormatter.getRepeatString(getActivity(), getResources(), rec, false);
                        ((TextView) view).setText(str);

                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
				return false;
			}
	    });


	    // Define action when clicking on an Activity item 
	    lv.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.v("DEBUG", "Activity "+ id +" has been clicked");
                Cursor cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);

				int activity_id = cursor.getInt(MyGoalsProvider.ACTIVITY_ID_INDEX);
				viewActivity(cursor, activity_id);
	    	}
	    });	    
	    
	    // Add a footer to the list to add new goals
	    View footer = mInflater.inflate(R.layout.footer, null);
	    TextView footerText=((TextView)footer.findViewById(R.id.t_footer_text));
	    footerText.setHint(R.string.footer_add_activity);
	    footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
        		openNewActivity();
			}
		});	    

	    lv.addFooterView(footer);
	    
	    lv.setAdapter(mAdapter);
		
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
		/*ContentResolver cr = getActivity().getContentResolver();
		Uri uri = Uri.parse(MyGoals.Activities.CONTENT_URI + "?goal_id=" +  ViewGoalActivity.mGoalId);
		mCursor = cr.query(uri, MyGoalsProvider.ACTIVITY_PROJECTION, null, null, null);

		mAdapter.changeCursor(mCursor);*/

        getLoaderManager().initLoader(0, null, this);
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
	
	private void viewActivity(Cursor c, int id) {
		Log.d(TAG,"viewActivity method");		
	    Intent intent = new Intent(getActivity(), ViewActivity.class);

	    intent.putExtra(ViewActivity.EXTRA_KEY_ID, id);
	    intent.putExtra(ViewActivity.EXTRA_KEY_GOAL_TITLE, ViewGoalActivity.mGoalTitle);
        intent.putExtra(ViewActivity.EXTRA_KEY_GOAL_ID, ViewGoalActivity.mGoalId);
        intent.putExtra(ViewActivity.EXTRA_KEY_TITLE, c.getString(MyGoalsProvider.ACTIVITY_TITLE_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_DESC, c.getString(MyGoalsProvider.ACTIVITY_DESC_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_START_DATE, c.getString(MyGoalsProvider.ACTIVITY_START_DATE_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_END_DATE, c.getString(MyGoalsProvider.ACTIVITY_END_DATE_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_DURATION, c.getString(MyGoalsProvider.ACTIVITY_DURATION_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_REPETITION, c.getInt(MyGoalsProvider.ACTIVITY_REPETITION_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_OCCURRENCE, c.getInt(MyGoalsProvider.ACTIVITY_OCCURRENCE_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_WEEKDAYS, c.getInt(MyGoalsProvider.ACTIVITY_WEEKDAYS_INDEX));
	    intent.putExtra(ViewActivity.EXTRA_KEY_NB_TASKS, c.getInt(MyGoalsProvider.ACTIVITY_NB_TASKS_INDEX));
	    
	    startActivityForResult(intent, 0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			ViewActivity.result res = (ViewActivity.result)data.getExtras().get(ViewActivity.EXTRA_KEY_RESULT);
			switch (res) {
			case DELETION:
				Toast.makeText(getActivity(), getResources().getText(R.string.Activity_deleted), Toast.LENGTH_SHORT).show();
				break;
			default:
			}
		}
	}


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG,"onCreateLoader");

        Uri uri = Uri.parse(MyGoals.Activities.CONTENT_URI + "?goal_id=" + ViewGoalActivity.mGoalId);

        return new CursorLoader(getActivity(), uri, MyGoalsProvider.ACTIVITY_PROJECTION, null, null, null);
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
