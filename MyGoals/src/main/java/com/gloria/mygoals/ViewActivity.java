package com.gloria.mygoals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import be.billington.calendar.recurrencepicker.EventRecurrence;

public class ViewActivity extends Activity {
	// For log purpose
	private static final  String TAG = "ViewActivity"; 

	public static final String EXTRA_KEY_ID = "id";
	public static final String EXTRA_KEY_TITLE = "title";
	public static final String EXTRA_KEY_GOAL_TITLE = "goal_title";
    public static final String EXTRA_KEY_GOAL_ID = "goal_id";
	public static final String EXTRA_KEY_DESC = "description";
	public static final String EXTRA_KEY_START_DATE = "start_date";
	public static final String EXTRA_KEY_END_DATE = "end_date";
	public static final String EXTRA_KEY_DURATION = "duration";
	public static final String EXTRA_KEY_REPETITION = "repetition";
	public static final String EXTRA_KEY_OCCURRENCE = "occurrence";
	public static final String EXTRA_KEY_WEEKDAYS = "weekdays";
	public static final String EXTRA_KEY_NB_TASKS = "nb_task";
	
	public static final String EXTRA_KEY_RESULT = "result";
	public static enum result {DELETION}
	
	
	// the activity values
	public static int mActivityId; 		// the id of the displayed Activity
	public static String mActivityTitle;	// the title of the displayed Activity	
	
	// Views
	private TextView mVTitle;
	private TextView mVDescription;
	private TextView mVStartDate;
	private TextView mVEndDate;
	private TextView mVWorkload;
	private TextView mVDuration;
	private TextView mVHours;
	private TextView mVOccurrence;
	private TextView mVNbTask;
	private TextView mVUnit;

	// Values
	private Date mStartDate;
	private Date mEndDate;
	private String mGoalTitle;
    private int mGoalId;
	private Date mDuration;
	private int mOccurrence;
	private int mRepetition;
	private int mNbTask;
	private String mDescription;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate method");			
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);

		// Set the action bar. Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    getActionBar().setDisplayHomeAsUpEnabled(true);
        }     
    	
    	// get the views
    	mVTitle = (TextView)findViewById(R.id.t_goal_title);
    	mVDescription = (TextView)findViewById(R.id.t_description);
    	mVStartDate = (TextView)findViewById(R.id.t_start_date);
    	mVEndDate = (TextView)findViewById(R.id.t_end_date);
    	mVDuration = (TextView)findViewById(R.id.t_task_duration);
    	mVOccurrence = (TextView)findViewById(R.id.t_occurrence);
    	mVNbTask = (TextView)findViewById(R.id.t_nb_task); 
    	mVUnit = (TextView)findViewById(R.id.l_unit); 
    	
        // get the intent extra info about the selected activity 
    	mActivityId=getIntent().getIntExtra(ViewGoalActivity.EXTRA_KEY_ID, 0);             
        mActivityTitle = getIntent().getStringExtra(EXTRA_KEY_TITLE);
        mGoalTitle = getIntent().getStringExtra(EXTRA_KEY_GOAL_TITLE);
        mGoalId = getIntent().getIntExtra(EXTRA_KEY_GOAL_ID, 0);
        mDescription = getIntent().getStringExtra(EXTRA_KEY_DESC);
        mOccurrence = getIntent().getIntExtra(EXTRA_KEY_OCCURRENCE,0);   
        mRepetition = getIntent().getIntExtra(EXTRA_KEY_REPETITION,0);
        mNbTask = getIntent().getIntExtra(EXTRA_KEY_NB_TASKS,0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);

        String startDate = getIntent().getStringExtra(EXTRA_KEY_START_DATE);
        String endDate = getIntent().getStringExtra(EXTRA_KEY_END_DATE);
        String duration = getIntent().getStringExtra(EXTRA_KEY_DURATION);
        
		try {
			mStartDate = sdf.parse(startDate);
			mEndDate = sdf.parse(endDate);
			mDuration = sdf.parse(duration);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		
		// Set the goal title as activity title        
        setTitle(mActivityTitle);

        // init the views
		mVStartDate.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(mStartDate));
		mVEndDate.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(mEndDate));
		mVDuration.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mDuration));
		
		mVTitle.setText(mGoalTitle);
		mVDescription.setText(mDescription);
	
		mVOccurrence.setText(""+mOccurrence);
		mVNbTask.setText(""+mNbTask);
		
		switch (mRepetition) {
		case EditActivity.NONE:
			break;
		case EventRecurrence.DAILY:
			mVUnit.setText(R.string.days);				
			break;			
		case EventRecurrence.WEEKLY:
			mVUnit.setText(R.string.weeks);			
			break;			
		case EventRecurrence.MONTHLY:
			mVUnit.setText(R.string.months);			
			break;			
		default:
			break;
		}
		

  
    }
	
	
	@Override
    public void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_view, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");					
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        // TODO proper up navigation : NavUtils.navigateUpFromSameTask(this);
		        finish();
		        return true;
	        case R.id.action_delete:
	        	removeActivity();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
 
	private void removeActivity() {
		Log.d(TAG,"removeActivity method");			
		new AlertDialog.Builder(this)
	    .setTitle("Delete entry")
	    .setMessage("This activity and its tasks will be deleted. Do you agree?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	ContentResolver cr = getContentResolver();
	        	
	        	// delete the activity
	        	int nbRowActivity =  cr.delete(Uri.parse(MyGoals.Activities.CONTENT_ID_URI_BASE + "" + mActivityId), null, null);
	        	Log.d(TAG, "" + nbRowActivity + "activities have been successfully deleted");
	        	
	        	// delete the tasks of the activity
	        	// query the tasks of the activity
	        	int nbRowTask = 0;
	    		Uri uri = Uri.parse(MyGoals.Tasks.CONTENT_URI + "?activity_id=" + mActivityId);
	    		Cursor c = cr.query(uri, MyGoalsProvider.TASK_PROJECTION, null, null, null);

                // For goal workload computation purpose
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(mDuration);
                // TODO To use Date or Integer or ... for the durations
                int duration = calendar.get(GregorianCalendar.HOUR_OF_DAY);

                int workload=0;
	    		int taskId;
	    		while (c.moveToNext()) {
	    			taskId = c.getInt(0); // TODO index of "_ID" column
                    if (c.getString(c.getColumnIndex(MyGoals.Tasks.COLUMN_NAME_DONE)).equals(Boolean.TRUE.toString())) {
                        workload += duration;
                    }

	    			cr.delete(Uri.parse(MyGoals.Tasks.CONTENT_ID_URI_BASE + "" + taskId), null, null);
	    		}
	        	Log.d(TAG, "" + nbRowTask + "tasks have been successfully deleted");

                // Update the goal progress by subtracting the completed tasks workload
                uri = Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + mGoalId);
                Cursor goalCursor = cr.query(uri,
                        new String [] {MyGoals.Goals.COLUMN_NAME_PROGRESS},
                        null, null, null);

                goalCursor.moveToFirst();
                int progress = goalCursor.getInt(goalCursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_PROGRESS));
                progress -= workload;

                goalCursor.close();

                ContentValues values = new ContentValues();
                values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, progress);
                int nbRowGoal = cr.update(uri, values, null, null);

	        	// Return the result OK
	        	if (nbRowActivity>0 && nbRowTask >0 && nbRowTask >0 ) {
		        	getIntent().putExtra(EXTRA_KEY_RESULT, result.DELETION);
		        	setResult(RESULT_OK,getIntent());
	        	}
	        	
	        	// Close the Activity
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
