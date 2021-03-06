package com.gloria.mygoals;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
                    int duration;
                    try {
                        duration = Integer.parseInt(cursor.getString(columnIndex));
                        String dur = DateUtils.formatElapsedTime(duration);
                        if (duration<3600) {
                            ((TextView) view).setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.minutes));
                        } else {
                            ((TextView) view).setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.hours));                               }
                    }
                    catch(Exception e) {
                        return false;
                    }
					return true;
				}
				if (view.getId()==R.id.t_nb_task) {
					int nbTask;
					try {
						nbTask = Integer.parseInt(cursor.getString(columnIndex));
					}
					catch(Exception e) {
                        return false;
					}
					// it displays the number of tasks only it is greater than 1 
					if (nbTask > 1) {
						((TextView)view).setText(cursor.getString(columnIndex)+" x ");
					} else {
                        ((TextView)view).setText("");
                    }
					return true;
				}				
				if (view.getId()==R.id.progress) {
					((ProgressBar)view).setMax(cursor.getInt(cursor.getColumnIndex(MyGoals.Activities.COLUMN_NAME_NB_TASKS)));
					((ProgressBar)view).setProgress(cursor.getInt(columnIndex));
					return true;
				}
                if (view.getId()==R.id.t_recurrence_rule) {
                    String str="";
                    String rrule=cursor.getString(columnIndex);
                    if (null==rrule) {
                        str=getString(R.string.single_task_activity);
                    } else {
                        try {
                            EventRecurrence rec = new EventRecurrence();
                            rec.parse(rrule);
                            str = EventRecurrenceFormatter.getRepeatString(getActivity(), getResources(), rec, false);
                        } catch (Exception e) {
                        }
                    }
                    ((TextView) view).setText(str);
                    return true;
                }
				return false;
			}
	    });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Log.v(TAG, "Item "+id+" has been long clicked");

                new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getText(R.string.dialog_title_deleteActivity))
                    .setMessage(getResources().getText(R.string.dialog_message_deleteActivity))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Log.v(TAG, "Delete the activity at position: " + position);
                            removeActivity(position);
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

    private void removeActivity(int position) {
        Log.d(TAG, "removeActivity method");

        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        int activityId = cursor.getInt(cursor.getColumnIndex(MyGoals.Activities._ID));

        ContentResolver cr = getActivity().getContentResolver();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);

        // delete the activity
        int nbRowActivity = cr.delete(Uri.parse(MyGoals.Activities.CONTENT_ID_URI_BASE + "" + activityId), null, null);
        Log.d(TAG, "" + nbRowActivity + "activities have been successfully deleted");

        // delete the tasks of the activity
        // query the tasks of the activity
        int nbRowTask = 0;
        Uri uri = Uri.parse(MyGoals.Tasks.CONTENT_URI + "?activity_id=" + activityId);
        Cursor c = cr.query(uri, MyGoalsProvider.TASK_PROJECTION, null, null, null);

        // Get the task duration in the activity tuple for the goal workload computation
        int duration = cursor.getInt(MyGoalsProvider.ACTIVITY_DURATION_INDEX);

        int activity_progress = 0;
        int taskId;
        while (c.moveToNext()) {
            taskId = c.getInt(0); // TODO index of "_ID" column
            if (c.getString(c.getColumnIndex(MyGoals.Tasks.COLUMN_NAME_DONE)).equals(Boolean.TRUE.toString())) {
                activity_progress += duration;
            }
            cr.delete(Uri.parse(MyGoals.Tasks.CONTENT_ID_URI_BASE + "" + taskId), null, null);
        }
        Log.d(TAG, "" + nbRowTask + "tasks have been successfully deleted");

        // Update the goal progress & workload by subtracting the completed tasks workload
        int goal_id = cursor.getInt(MyGoalsProvider.ACTIVITY_GOAL_ID_INDEX);
        int nb_tasks = cursor.getInt(MyGoalsProvider.ACTIVITY_NB_TASKS_INDEX);

        uri = Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + goal_id);
        Cursor goalCursor = cr.query(uri,
                new String[]{MyGoals.Goals.COLUMN_NAME_PROGRESS,MyGoals.Goals.COLUMN_NAME_WORKLOAD},
                null, null, null);

        goalCursor.moveToFirst();
        int progress = goalCursor.getInt(goalCursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_PROGRESS));
        int workload = goalCursor.getInt(goalCursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_WORKLOAD));

        progress -= activity_progress;
        workload -= nb_tasks * duration;

        goalCursor.close();

        ContentValues values = new ContentValues();
        values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, progress);
        values.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD, workload);
        int nbRowGoal = cr.update(uri, values, null, null);

        // Return the result OK
        if (nbRowGoal > 0 && nbRowActivity > 0 && nbRowTask > 0) {
            //getIntent().putExtra(EXTRA_KEY_RESULT, result.DELETION);
            //setResult(RESULT_OK, getIntent());
        }
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		/*if (resultCode == Activity.RESULT_OK) {
			ViewActivity.result res = (ViewActivity.result)data.getExtras().get(ViewActivity.EXTRA_KEY_RESULT);
			switch (res) {
			case DELETION:
				Toast.makeText(getActivity(), getResources().getText(R.string.Activity_deleted), Toast.LENGTH_SHORT).show();
				break;
			default:
			}
		}*/
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
