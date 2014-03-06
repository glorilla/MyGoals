package com.gloria.mygoals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.Duration;

import com.gloria.mygoals.EditGoalActivity.Mode;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;

public class EditActivity extends Activity implements usesDatePickerDialogInterface, usesTimePickerDialogInterface {
	
	// Inner class TaskDates stores the start date&time and the end date&time of a task
	private class TaskDates {
		// 0-based index
		public int index;
		public Date startDate;
		public Date endDate;
		
		public TaskDates(int index, Date startDate, Date endDate) {
			this.index=index;
			this.startDate=startDate;
			this.endDate=endDate;
		}
	}

	// For log purpose	
	private final static String TAG = "EditActivity";
	
	// Frequency spinner choices 
	static final int NONE=0;
	static final int DAILY=1;
	static final int WEEKLY=2;
	static final int MONTHLY=3;
	
	// Intent extra parameters' interface
	static final String EXTRA_KEY_MODE = "mode";
	static final String EXTRA_KEY_GOAL_ID = "goal_id";
	static final String EXTRA_KEY_GOAL_TITLE = "goal_title";	
	static enum Mode {NEW, EDIT};
	
	// activity state
	private Mode mMode=Mode.NEW;
	private int mGoalId=0;
	private String mGoalTitle;

	// form views
	private TextView mVGoalTitle;
	private EditText mVActivityTitle;	
	private EditText mVActivityDesc;
	private TextView mVTaskDuration;
	private TextView mVStartDate;
	private TextView mVStartTime;
	private TextView mVEndDate;
	private Spinner mVFrequencyChoice;
	private EditText mVOccurence;
	private EditText mVNbTasks;
	// TODO To add the workload field in the activity table
	private TextView mVWorkload;	
	private TextView mVFreqUnit;
	private Button mSubmitBtn;
	private RadioButton mVEndDateRadioBtn;
	private RadioButton mVNbTaskRadioBtn;
	
	// form layout
	private ViewGroup mRepetitionLayout;
	private ViewGroup mWeekLayout;
	private ViewGroup mFrequencyLayout;
	private final int[] mVCheckboxes = {
		R.id.c_monday, R.id.c_tuesday, R.id.c_wednesday, R.id.c_thrusday,
		R.id.c_friday, R.id.c_saturday, R.id.c_sunday
	};
	
	// form values
	private Date mStartDate;
	private Date mStartTime;
	private Date mDuration;
	private Date mEndDate;
	private int mFreqSpinnerPos=NONE;
	private int mNbTasks;
	private int mOccurrence;
	private String mWeekdays;
	private boolean bFixedNbTask;
	
	// for DatePickerDialog purpose
	private Date mCurrentDate;
	private TextView mCurrentDateTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate method");					
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		setTitle(getResources().getString(R.string.my_activity));
		
		// Set the mode and the Goal id thanks to the intent extra values	
		mMode = (Mode)getIntent().getExtras().get(EXTRA_KEY_MODE);
		mGoalId = getIntent().getIntExtra(EXTRA_KEY_GOAL_ID,0);
		mGoalTitle = getIntent().getStringExtra(EXTRA_KEY_GOAL_TITLE);
		
		// Set the action bar
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    getActionBar().setDisplayHomeAsUpEnabled(true);
        }
		
		// Init the views' values and listeners
        initViews();		
	}

	private void initViews() {
		Log.d(TAG,"initViews method");			

		// Get the layouts
		mRepetitionLayout = (ViewGroup) findViewById(R.id.repetition_layout);
		mWeekLayout = (ViewGroup) findViewById(R.id.layout_week);
		mFrequencyLayout = (ViewGroup) findViewById(R.id.layout_frequency);
		
        // Get the layout's views
        mVGoalTitle = (TextView) findViewById(R.id.l_activity_goalTitle);
        mVActivityTitle = (EditText) findViewById(R.id.t_activity_title);
        mVActivityDesc = (EditText) findViewById(R.id.t_activity_description);
        mVTaskDuration = (TextView) findViewById(R.id.t_task_duration);
    	mVStartDate = (TextView) findViewById(R.id.t_activity_start_date);
    	mVStartTime = (TextView) findViewById(R.id.t_activity_start_time);
    	mVEndDate = (TextView) findViewById(R.id.t_activity_end_date);
    	mVFrequencyChoice = (Spinner) findViewById(R.id.sp_frequency);
    	mVOccurence = (EditText) findViewById(R.id.t_occurence);
    	mVNbTasks = (EditText) findViewById(R.id.t_nb_task);
    	mVWorkload = (TextView) findViewById(R.id.l_activity_workload);
		mVFreqUnit = (TextView) findViewById(R.id.t_freq_unit);
		mVEndDateRadioBtn = (RadioButton) findViewById(R.id.c_activity_end_date);
		mVNbTaskRadioBtn = (RadioButton) findViewById(R.id.c_nb_task);
				
		mSubmitBtn = (Button) findViewById(R.id.btn_submit);
		
		// Set the Goal Title
		mVGoalTitle.setText(mGoalTitle);
		
		// Set the submit button label and init the fields values
		switch (mMode) {
			case NEW:	// Set the label "Create" on the submit button
				mSubmitBtn.setText(R.string.create);
				fillWithDefaultValues();
				break;
			case EDIT:	// Set the label "Modify" on the submit button
				mSubmitBtn.setText(R.string.modify);				
				// TODO Modification Mode: fillWithActivityValues(mActivityId);
				break;
			default:
		}
		
		// Set the view listeners		
		mSubmitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"onClick method on the submit button");					
				// validate the form values
				if (validateForm()) {
					switch (mMode) {
					case NEW:
						insertInDB();
						break;
					case EDIT:
						// TODO Modification Mode: updateInDB(mGoalId);
						break;
					}	
					EditActivity.this.finish();		// quit the activity
				}	
			}
		});
		
		mVStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG,"onClick method on the Start Date textView");						
				// Init the view's references 
				mCurrentDateTextView=(TextView)v;
				mCurrentDate=mStartDate;
				// Show the DatePickerDialog
				showDatePickerDialog();
			}
		});
		
		
		mVStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG,"onClick method on the Start Time textView");
				Log.v(TAG,"The id of the clicked view is " + v.getId());				
				// Init the view's references 
				mCurrentDateTextView=(TextView)v;
				mCurrentDate=mStartDate;
				// Show the DatePickerDialog
				showTimePickerDialog();
			}
		});
		
		mVTaskDuration.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG,"onClick method on the task duration textView");
				Log.v(TAG,"The id of the clicked view is " + v.getId());				
				// Init the view's references 
				mCurrentDateTextView=(TextView)v;
				mCurrentDate=mDuration;
				// Show the DatePickerDialog
				showTimePickerDialog();
			}
		});

		mVEndDateRadioBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// Enable or Disable the focus on the associated view
				if(isChecked) {
					bFixedNbTask=false;
					mVEndDate.setFocusable(true);
					mVEndDate.setFocusableInTouchMode(true);
 				} else {
					mVEndDate.setFocusable(false);
 					mVEndDate.setFocusableInTouchMode(false); 					
 				}
			}
		});

		mVNbTaskRadioBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// Enable or Disable the focus on the associated view
				if(isChecked) {
					bFixedNbTask=true;					
					mVNbTasks.setFocusable(true);
					mVNbTasks.setFocusableInTouchMode(true);
 				} else {
					mVNbTasks.setFocusable(false);
 					mVNbTasks.setFocusableInTouchMode(false); 					
 				}				
			}
		});		

		
		mVEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG,"onClick method on the End Date textView");		
				// Init the view's references 
				mCurrentDateTextView=(TextView)v;
				mCurrentDate=mEndDate;
				// Show the DatePickerDialog
				showDatePickerDialog();
			}
		});
		
		mVOccurence.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				computeListOfTasks();

				// Update the Activity's end date & nb of tasks
				mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
				mVNbTasks.setText("" + mNbTasks);					
			}
		});
		
		mVNbTasks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				computeListOfTasks();
				
				// Update the Activity's end date & nb of tasks
				mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
				mVNbTasks.setText("" + mNbTasks);					
			}
		});
		
		mVFrequencyChoice.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mFreqSpinnerPos=position;
				// To implement the panes' visibility management
				switch (position) {
					case NONE:
						mRepetitionLayout.setVisibility(View.GONE);
						break;
					case DAILY:
						// In case of daily repetition, the user can only choose a frequency
						mRepetitionLayout.setVisibility(View.VISIBLE);
						mFrequencyLayout.setVisibility(View.VISIBLE);
						mWeekLayout.setVisibility(View.GONE);
						// Set the frequency unity to days
						mVFreqUnit.setText(R.string.day);
						break;
					case WEEKLY:
						// In case of weekly repetition, the user can choose a frequency and the days on the week 
						mRepetitionLayout.setVisibility(View.VISIBLE);
						mFrequencyLayout.setVisibility(View.VISIBLE);
						mWeekLayout.setVisibility(View.VISIBLE);
						// Set the frequency unity to weeks
						mVFreqUnit.setText(R.string.week);						
						break;
					case MONTHLY:
						// TODO Utility of monthly repetition? Every 1st Monday of the month? in interaction with the start date
						mRepetitionLayout.setVisibility(View.VISIBLE);
						mFrequencyLayout.setVisibility(View.GONE);
						mWeekLayout.setVisibility(View.GONE);						
						break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
			
	}	

	private boolean validateForm() {
		Log.d(TAG,"validateForm method");		
		/* TODO To implement the form validation before submitting
		 	- are all mandatory fields filled ?
		 	- is start date < end date ?
		 */
		return true;
		//return false;
	}
	
	private void insertInDB() {
		Log.d(TAG,"insertInDB method");

		// TODO Raise an exception if not inserted in DB
		Uri res = insertActivityInDB();
		// TODO Raise an exception if not a parseable string
		int activityId = Integer.parseInt(res.getPathSegments().get(1));
		
		// Array of TaskDates to store the start date&time and end date&time of the tasks 
		ArrayList<TaskDates> tasks = computeListOfTasks();		
		
		for(TaskDates task: tasks) {
			insertTaskInDB(activityId, task);
		}
	}

	private Uri insertActivityInDB() {
		Log.d(TAG,"insertActivityInDB method");
		
		// Query the DB through a contentProvider, though use a contentResolver
		ContentResolver cr = getContentResolver();
		ContentValues values=new ContentValues();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		values.put(MyGoals.Activities.COLUMN_NAME_GOAL_ID, mGoalId);
		values.put(MyGoals.Activities.COLUMN_NAME_TITLE, mVActivityTitle.getText().toString());
		values.put(MyGoals.Activities.COLUMN_NAME_DESC, mVActivityDesc.getText().toString());

		//Dates are stored as UTC date strings ("YYYY-MM-DD HH:mmZ")
		values.put(MyGoals.Activities.COLUMN_NAME_START_DATE, sdf.format(mStartDate));
		values.put(MyGoals.Activities.COLUMN_NAME_DURATION, sdf.format(mDuration));
		values.put(MyGoals.Activities.COLUMN_NAME_REPETITION, mFreqSpinnerPos);
		
		// The week checkbox values are stored in DB as a String of boolean values eg. "true&false&..."
		mWeekdays = "" + ((CheckBox)findViewById(mVCheckboxes[0])).isChecked();
		for (int i=1; i<mVCheckboxes.length; i++) {
			mWeekdays += "&" + ((CheckBox)findViewById(mVCheckboxes[i])).isChecked();
		}
		
		switch (mFreqSpinnerPos) {
			case NONE:
				// The end date is the end of the unique task: start date + the task duration
				values.put(MyGoals.Activities.COLUMN_NAME_END_DATE, sdf.format(mEndDate));
				mNbTasks = 1;
				mOccurrence = 1;
				mWeekdays = "";
				break;
			case DAILY:
				/* TODO To manage end date value, if not defined it should be start date + duration
		 		or the last task date */
				values.put(MyGoals.Activities.COLUMN_NAME_END_DATE, sdf.format(mEndDate));
				mNbTasks = Integer.parseInt(""+mVNbTasks.getText());
				mOccurrence = Integer.parseInt(""+mVOccurence.getText().toString());
				mWeekdays = "";
				break;
			case WEEKLY:
				/* TODO To manage end date value, if not defined it should be start date + duration
		 		or the last task date */
				values.put(MyGoals.Activities.COLUMN_NAME_END_DATE, sdf.format(mEndDate));
				mNbTasks = Integer.parseInt(""+mVNbTasks.getText());
				mOccurrence = Integer.parseInt(""+mVOccurence.getText().toString());
				break;
			case MONTHLY:
				/* TODO To manage end date value, if not defined it should be start date + duration
		 		or the last task date */

				values.put(MyGoals.Activities.COLUMN_NAME_END_DATE, sdf.format(mEndDate));
				mNbTasks = Integer.parseInt(""+mVNbTasks.getText());
				mOccurrence = 1;
				mWeekdays = "";
				break;
		}
		values.put(MyGoals.Activities.COLUMN_NAME_NB_TASKS, mNbTasks);
		values.put(MyGoals.Activities.COLUMN_NAME_OCCURRENCE, mOccurrence);
		values.put(MyGoals.Activities.COLUMN_NAME_WEEKDAYS, mWeekdays);
		
		return cr.insert(MyGoals.Activities.CONTENT_URI, values);

	}		
	
	private Uri insertTaskInDB(int activityId, TaskDates task) {
		Log.d(TAG,"insertActivityInDB method");
		
		// Query the DB through a contentProvider, though use a contentResolver
		ContentResolver cr = getContentResolver();
		ContentValues values=new ContentValues();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		String taskTitle=mVActivityTitle.getText().toString();
		if (mNbTasks > 1) {
			taskTitle += " ("+ (task.index + 1) + "/" + mNbTasks + ")";
		};
		values.put(MyGoals.Tasks.COLUMN_NAME_TITLE,  taskTitle);
		values.put(MyGoals.Tasks.COLUMN_NAME_GOAL_ID, mGoalId);
		values.put(MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID, activityId);
		values.put(MyGoals.Tasks.COLUMN_NAME_DUE_DATE, sdf.format(task.endDate));
		values.put(MyGoals.Tasks.COLUMN_NAME_START_DATE, sdf.format(task.startDate));

		values.put(MyGoals.Tasks.COLUMN_NAME_DONE, Boolean.FALSE.toString());
		// TODO missing default values for MyGoals.Tasks.COLUMN_NAME_DONE_DATE; MyGoals.Tasks.COLUMN_NAME_STATUS;
		
		return cr.insert(MyGoals.Tasks.CONTENT_URI, values);
		
	}	
	
	private void fillWithDefaultValues() {
		Log.d(TAG,"fillWithDefaultValues method");		
	
		// init the start date & time with the current date & time
		mStartDate=new Date();
		mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
		mVStartTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mStartDate));

		// init the task duration to 1:00
		GregorianCalendar duration = new GregorianCalendar(1, 0, 1, 1, 0);
		mDuration=duration.getTime();
		mVTaskDuration.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mDuration));
		
		// Compute the task's end date		
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.setTime(mStartDate);
		endDate.add(Calendar.HOUR,duration.get(Calendar.HOUR));
		endDate.add(Calendar.MINUTE,duration.get(Calendar.MINUTE));
		mEndDate=endDate.getTime();
		mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
		
		// Default spinner value and panes' visibility
		mVFrequencyChoice.setSelection(mFreqSpinnerPos);
		
		mNbTasks = 1;
		mOccurrence = 1;
		mWeekdays = "";
		
	}

	private ArrayList<TaskDates> computeListOfTasks() {
		
		// the result object
		ArrayList<TaskDates> listOfTask=new ArrayList<TaskDates>();
		
		// Convert the duration to a calendar object
		GregorianCalendar duration = new GregorianCalendar();
		duration.setTime(mDuration);
		
		// Convert the activity's end date to a calendar object
		GregorianCalendar activityEnd = new GregorianCalendar();
		activityEnd.setTime(mEndDate);
		
		// Convert the task's start date to a calendar object		
		GregorianCalendar startDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();
		startDate.setTime(mStartDate);
		
		// Compute the task's end date		
		endDate.setTime(mStartDate);
		endDate.add(Calendar.HOUR_OF_DAY,duration.get(Calendar.HOUR_OF_DAY));
		endDate.add(Calendar.MINUTE,duration.get(Calendar.MINUTE));
		
		// Set the period unity between 2 consecutive tasks
		int unity=Calendar.DAY_OF_YEAR;
		switch(mFreqSpinnerPos) {
		case NONE:
			listOfTask.add(new TaskDates(1, startDate.getTime(), endDate.getTime()));
			return listOfTask;
		case WEEKLY:
			unity=Calendar.WEEK_OF_YEAR;
			break;
		case MONTHLY:
			unity=Calendar.MONTH;			
			break;
		case DAILY:
		} 
		
		mOccurrence=Integer.parseInt(mVOccurence.getText().toString());
		mNbTasks=Integer.parseInt(mVNbTasks.getText().toString());
		
		// Tasks are determined in function of the activity's end date 
		if (!bFixedNbTask) {
			int i=0;
			while ( endDate.before(activityEnd) || endDate.equals(activityEnd) ) {
				// Add the task to the result list
				listOfTask.add(new TaskDates(i++, startDate.getTime(), endDate.getTime()));
				// TODO to implement the week days feature
				// Increment the dates for the next task
				startDate.add(unity, mOccurrence);
				endDate.add(unity, mOccurrence);				
			}
			// Update the number of tasks
			mNbTasks = listOfTask.size();
		}
		// Tasks are determined in function of the Activity's number of tasks 
		else {
			for (int i=0; i<mNbTasks; i++) {
				// Add the task to the result list
				listOfTask.add(new TaskDates(i, startDate.getTime(), endDate.getTime()));
				// TODO to implement the week days feature
				// Increment the dates for the next task
				startDate.add(unity, mOccurrence);
				endDate.add(unity, mOccurrence);						
			}
			// Update the Activity's end date
			mEndDate=listOfTask.get(listOfTask.size()-1).endDate;
		}
		
		return listOfTask;
	}
	
	public void showDatePickerDialog() {
		Log.d(TAG,"showDatePickerDialog method");		

		DialogFragment dialog = new DatePickerFragment();
	    dialog.show(getFragmentManager(), "datePicker");
	}

	public void showTimePickerDialog() {
		Log.d(TAG,"showTimePickerDialog method");		

		DialogFragment dialog = new TimePickerFragment();
	    dialog.show(getFragmentManager(), "timePicker");
	}
	
	@Override
	// Used to init the default date or time in the date or time pickers
	public Date getCurrentDate() {
		return mCurrentDate;
	}

	@Override
	// called when a date is set in a date picker
	public void DatePickerCallBack(int year, int month, int day) {
		if (mCurrentDateTextView == mVStartDate) {
			// Format the date to the user's locales and set the destination view
			final Calendar c = Calendar.getInstance();
			c.setTime(mStartDate);			
			c.set(year, month, day);
			mStartDate.setTime(c.getTimeInMillis());
			mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
			// Recompute the View parameters
			computeListOfTasks();
			// Update the Activity's end date & nb of tasks
			mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
			mVNbTasks.setText("" + mNbTasks);
		} else if (mCurrentDateTextView == mVEndDate) {
			// Format the date to the user's locales and set the destination view
			final Calendar c = Calendar.getInstance();
			c.setTime(mEndDate);			
			c.set(year, month, day);
			mEndDate.setTime(c.getTimeInMillis());
			mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));	
			// Recompute the View parameters
			computeListOfTasks();
			// Update the Activity's nb of tasks
			mVNbTasks.setText("" + mNbTasks);
		} else {
			Log.w(TAG,"Setting a date in a DatePicker dialog should update a View");			
		}
		
	}
	
	@Override
	// called when a time is set in a time picker	
	public void TimePickerCallBack(int hourOfDay, int minute) {
		if (mCurrentDateTextView == mVStartTime) {
			// Format the date to the user's locales and set the destination view
			final Calendar c = Calendar.getInstance();
			c.setTime(mStartDate);
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			mStartDate.setTime(c.getTimeInMillis());
			mVStartTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mStartDate));
			// Recompute the View parameters
			computeListOfTasks();
			// Update the Activity's end date & nb of tasks
			mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
			mVNbTasks.setText("" + mNbTasks);		
		} else if (mCurrentDateTextView == mVTaskDuration) {
			// Format the date to the user's locales and set the destination view
			final Calendar c = Calendar.getInstance();
			c.set(1, 0, 1, hourOfDay, minute);
			mDuration.setTime(c.getTimeInMillis());
			mVTaskDuration.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mDuration));
			// Recompute the View parameters
			computeListOfTasks();
			// Update the Activity's end date & nb of tasks
			mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
			mVNbTasks.setText("" + mNbTasks);				
		} else {
			Log.w(TAG,"Setting a time in a TimePicker dialog should update a View");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,"onCreateOptionsMenu method");
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu., menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionsItemSelected method");					
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        // TODO proper Up Navigation : NavUtils.navigateUpFromSameTask(this);
		        finish();
		        return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
