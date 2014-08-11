package com.gloria.mygoals;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class EventListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	// For log purpose
	private static final  String TAG = "EventListFragment";
	
	private final String[] eventState = {"Planned", "On Track", "Completed", "Late", "In Advance"};
	
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
		mInflater=inflater;
		mRootView = inflater.inflate(R.layout.event_list, container, false);
		
        ListView lv= (ListView)mRootView.findViewById(R.id.lv_events); // get the listview in the view hierarchy
        
		//ContentResolver cr = getActivity().getContentResolver();
		//mCursor = cr.query(MyGoals.Tasks.CONTENT_URI, MyGoalsProvider.TASK_PROJECTION, null, null, null);
		
		String[] from = new String[] {
				MyGoals.Tasks.COLUMN_NAME_START_DATE,
				MyGoals.Tasks.COLUMN_NAME_START_DATE,				
				MyGoals.Tasks.COLUMN_NAME_DUE_DATE,
				MyGoals.Tasks.COLUMN_NAME_TITLE,
				MyGoals.Tasks.COLUMN_NAME_GOAL_TITLE,
				MyGoals.Tasks.COLUMN_NAME_DONE,
				MyGoals.Tasks.COLUMN_NAME_STATUS,
                MyGoals.Tasks.COLUMN_NAME_GOAL_COLOR
				};
	    
		int[] to = new int[] { 
	    		R.id.t_event_date,
	    		R.id.t_start_time,	    		
	    		R.id.t_end_time,
	    		R.id.t_event_title, 
	    		R.id.t_event_goal,
	    		R.id.b_event_done,
	    		R.id.t_progress,
                R.id.root_layout
	    		};

	    // fill in the activity list layout
	    mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.event_item, /*mCursor*/null, from, to, 0);

	    mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				if (view.getId()==R.id.t_event_date){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);

					try {
						Date startDate = sdf.parse(cursor.getString(columnIndex));
						((TextView)view).setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(startDate));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				if (view.getId()==R.id.t_start_time) {				
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);

					try {
						Date startTime = sdf.parse(cursor.getString(columnIndex));
						((TextView)view).setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(startTime));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						return true;
				}					
				if (view.getId()==R.id.t_end_time) {				
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);

					try {
						Date endTime = sdf.parse(cursor.getString(columnIndex));
						((TextView)view).setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(endTime));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						return true;
				}				
				if (view.getId()==R.id.b_event_done){				
					// TODO Add try catch block
					// set the cursor position in the check box's tag to associate them together
					view.setTag(cursor.getPosition());
					
					((CheckBox)view).setOnCheckedChangeListener(null);
					
					boolean checked = Boolean.parseBoolean(cursor.getString(columnIndex));
					((CheckBox)view).setChecked(checked);
					((CheckBox)view).setClickable(!checked);
					
					((CheckBox)view).setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

							if (isChecked) {
								// lock the task's checkbox
								buttonView.setClickable(false);

								// set the task as completed
								int position = (Integer)(buttonView.getTag());
								setTaskCompleted(position);
								
								// Refresh the view
								//mCursor.requery();
							}
						}
					});
					
					return true;
				}
				if (view.getId()==R.id.t_progress){				
					((TextView)view).setText(eventState[Integer.parseInt(cursor.getString(columnIndex))]);
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
    			Log.v(TAG, "Item "+id+" has been clicked");
    			
        	}
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Log.v(TAG, "Item "+id+" has been long clicked");

                // get the completion state of the task
                CheckBox v = (CheckBox)view.findViewById(R.id.b_event_done);
                if (v.isChecked()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.dialog_title_uncheckTask)
                            .setMessage(R.string.dialog_message_uncheckTask)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with uncheck
                                    Log.v(TAG, "Uncheck the task at position " + position);

                                    setTaskNotCompleted(position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.alert_warning)
                            .show();

                    return true;
                }

            return true;
            }
        });

	    // Add a footer to the list to add new tasks
	    /*View footer = mInflater.inflate(R.layout.footer, null);
	    TextView footerText=((TextView)footer.findViewById(R.id.t_footer_text));
	    footerText.setHint(R.string.footer_add_task);
	    footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
        		openNewTaskActivity();
			}
		});	    

	    lv.addFooterView(footer);*/
	    
	    lv.setAdapter(mAdapter);
	    
		return mRootView;
	}	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }
    
    @Override    
    public void onDestroyView() {
    	//mCursor.close();
    	super.onDestroyView();
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
	     /*   case R.id.action_new:
        		openNewTaskActivity();
	            return true;*/
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	}
 
	private void openNewTaskActivity() {
		Log.d(TAG,"openNewTaskActivity method");				
	    /* TODO to implement the method to create a new task 
	    Intent intent= new Intent(getActivity(), EditGoalActivity.class);
	    intent.putExtra(EditGoalActivity.EXTRA_KEY_MODE, EditGoalActivity.Mode.NEW);
	    startActivity(intent);*/
	}	    
    
	
	private void setTaskCompleted(int position) {

		Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);

		int taskId = cursor.getInt(cursor.getColumnIndex(MyGoals.Tasks._ID));

		// TODO determine the task's status
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		ContentResolver cr = getActivity().getContentResolver();
		ContentValues values= new ContentValues();
		
		// update the task
		Uri uri = Uri.parse(MyGoals.Tasks.CONTENT_ID_URI_BASE + "" + taskId);
		values.put(MyGoals.Tasks.COLUMN_NAME_DONE_DATE, sdf.format(new Date()));
		values.put(MyGoals.Tasks.COLUMN_NAME_DONE, Boolean.TRUE.toString());
		// TODO Update the task' status
		int nbTask = cr.update(uri, values, null, null);

		// update the activity's progress
		int activityId = cursor.getInt(cursor.getColumnIndex(MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID));
		uri = Uri.parse(MyGoals.Activities.CONTENT_ID_URI_BASE + "" + activityId);
		Cursor activityCursor = cr.query(uri, 
				new String [] {MyGoals.Activities.COLUMN_NAME_PROGRESS, MyGoals.Activities.COLUMN_NAME_DURATION}, 
				null, null, null);
		
		activityCursor.moveToFirst();
		int progress = activityCursor.getInt(activityCursor.getColumnIndex(MyGoals.Activities.COLUMN_NAME_PROGRESS));
		String sDuration = activityCursor.getString(activityCursor.getColumnIndex(MyGoals.Activities.COLUMN_NAME_DURATION));
		GregorianCalendar calendar = new GregorianCalendar();
		try {
			calendar.setTime(sdf.parse(sDuration));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO To use Date or Integer or ... for the durations 
		int duration = calendar.get(GregorianCalendar.HOUR_OF_DAY); 
		progress += 1; 
		
		activityCursor.close();
		
		values.clear();
		values.put(MyGoals.Activities.COLUMN_NAME_PROGRESS, progress);
		int nbActivity = cr.update(uri, values, null, null);		
		
		// update the goal's progress
		int goalId = cursor.getInt(cursor.getColumnIndex(MyGoals.Tasks.COLUMN_NAME_GOAL_ID));
		uri = Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + goalId);
		Cursor goalCursor = cr.query(uri, 
				new String [] {MyGoals.Goals.COLUMN_NAME_PROGRESS},
				null, null, null);		
		
		goalCursor.moveToFirst();
		progress = goalCursor.getInt(goalCursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_PROGRESS));
		progress += duration;
		
		goalCursor.close();
				
		values.clear();
		values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, progress);
		int nbGoal = cr.update(uri, values, null, null);
		
		if (nbTask != 1 || nbActivity != 1 || nbGoal != 1) {
			Log.v(TAG, "Update error");
		}
	}

    private void setTaskNotCompleted(int position) {

        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);

        int taskId = cursor.getInt(cursor.getColumnIndex(MyGoals.Tasks._ID));

        // TODO determine the task's status

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values= new ContentValues();

        // update the task
        Uri uri = Uri.parse(MyGoals.Tasks.CONTENT_ID_URI_BASE + "" + taskId);
        values.put(MyGoals.Tasks.COLUMN_NAME_DONE_DATE, "");
        values.put(MyGoals.Tasks.COLUMN_NAME_DONE, Boolean.FALSE.toString());
        // TODO Update the task' status
        int nbTask = cr.update(uri, values, null, null);

        // update the activity's progress
        int activityId = cursor.getInt(cursor.getColumnIndex(MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID));
        uri = Uri.parse(MyGoals.Activities.CONTENT_ID_URI_BASE + "" + activityId);
        Cursor activityCursor = cr.query(uri,
                new String [] {MyGoals.Activities.COLUMN_NAME_PROGRESS, MyGoals.Activities.COLUMN_NAME_DURATION},
                null, null, null);

        activityCursor.moveToFirst();
        int progress = activityCursor.getInt(activityCursor.getColumnIndex(MyGoals.Activities.COLUMN_NAME_PROGRESS));
        String sDuration = activityCursor.getString(activityCursor.getColumnIndex(MyGoals.Activities.COLUMN_NAME_DURATION));
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(sdf.parse(sDuration));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // TODO To use Date or Integer or ... for the durations
        progress -= 1;

        activityCursor.close();

        values.clear();
        values.put(MyGoals.Activities.COLUMN_NAME_PROGRESS, progress);
        int nbActivity = cr.update(uri, values, null, null);

        // update the goal's progress
        int goalId = cursor.getInt(cursor.getColumnIndex(MyGoals.Tasks.COLUMN_NAME_GOAL_ID));
        uri = Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + goalId);
        Cursor goalCursor = cr.query(uri,
                new String [] {MyGoals.Goals.COLUMN_NAME_PROGRESS},
                null, null, null);

        goalCursor.moveToFirst();
        int duration = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        progress = goalCursor.getInt(goalCursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_PROGRESS));
        progress -= duration;

        goalCursor.close();

        values.clear();
        values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, progress);
        int nbGoal = cr.update(uri, values, null, null);

        if (nbTask != 1 || nbActivity != 1 || nbGoal != 1) {
            Log.v(TAG, "Update error");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG,"onCreateLoader");
        return new CursorLoader(getActivity(), MyGoals.Tasks.CONTENT_URI, MyGoalsProvider.TASK_PROJECTION, null, null, null);
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
