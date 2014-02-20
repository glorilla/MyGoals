package com.gloria.mygoals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.gloria.mygoals.EditGoalActivity.Mode;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;

public class EditActivity extends Activity implements usesDatePickerDialogInterface {
	// For log purpose	
	private final static String TAG = "EditActivity";
	
	// Frequency spinner choices 
	static final int NONE=0;
	static final int DAILY=1;
	static final int WEEKLY=2;
	static final int MONTHLY=3;
	
	// Intent extra parameters' interface
	static final String EXTRA_KEY_MODE = "mode";
	static final String EXTRA_KEY_ID = "id";
	static enum Mode {NEW, EDIT};
	
	// activity state
	private Mode mMode=Mode.NEW;
	private int mGoalId=0;

	// form views
	private TextView mVGoalTitle;
	private EditText mVActivityTitle;	
	private EditText mVActivityDesc;
	private EditText mVTaskDuration;
	private TextView mVStartDate;
	private TextView mVStartTime;
	private TextView mVEndDate;
	private Spinner mVFrequencyChoice;
	private EditText mVOccurence;
	private EditText mVNbTasks;
	private TextView mVWorkload;	
	private TextView mVFreqUnit;
	private Button mSubmitBtn;
	
	// form layout
	private ViewGroup mRepetitionLayout;
	private ViewGroup mWeekLayout;
	private ViewGroup mFrequencyLayout;
	
	// form values
	private Date mStartDate;
	private Date mStartTime;	
	private Date mEndDate;
	
	// for DatePickerDialog purpose
	private Date mCurrentDate;
	private TextView mCurrentDateTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate method");					
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		// Set the mode and the Goal id thanks to the intent extra values	
		/* TODO To adapt
		mMode = (Mode)getIntent().getExtras().get(EXTRA_KEY_MODE);
		mGoalId = getIntent().getIntExtra(EXTRA_KEY_ID,0);*/
		
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
        mVTaskDuration = (EditText) findViewById(R.id.t_task_duration);
    	mVStartDate = (TextView) findViewById(R.id.t_activity_start_date);
    	mVStartTime = (TextView) findViewById(R.id.t_activity_start_time);
    	mVEndDate = (TextView) findViewById(R.id.t_activity_end_date);
    	mVFrequencyChoice = (Spinner) findViewById(R.id.sp_frequency);
    	mVOccurence = (EditText) findViewById(R.id.t_occurence);
    	mVNbTasks = (EditText) findViewById(R.id.t_nb_task);
    	mVWorkload = (TextView) findViewById(R.id.l_activity_workload);
		mSubmitBtn = (Button) findViewById(R.id.btn_submit);
		mVFreqUnit = (TextView) findViewById(R.id.t_freq_unit);

		// Set the submit button label and init the fields values
		switch (mMode) {
			case NEW:	// Set the label "Create" on the submit button
				mSubmitBtn.setText(R.string.create);
				fillWithDefaultValues();
				break;
			case EDIT:	// Set the label "Modify" on the submit button
				mSubmitBtn.setText(R.string.modify);				
				// TODO Modification Mode: fillWithGoalValues(mGoalId);
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
				// Init the view's references 
				mCurrentDateTextView=(TextView)v;
				mCurrentDate=mStartTime;
				// Show the DatePickerDialog
				showTimePickerDialog();
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
		
		mVFrequencyChoice.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO To implement the panes' visibility management
				switch (position) {
					case NONE:
						mRepetitionLayout.setVisibility(View.GONE);
						break;
					case DAILY:
						// In case of daily repetition, the user can only choose a frequency
						mRepetitionLayout.setVisibility(View.VISIBLE);
						mFrequencyLayout.setVisibility(View.VISIBLE);
						mWeekLayout.setVisibility(View.GONE);
						// TODO Set the frequency unity to days
						mVFreqUnit.setText(R.string.day);
						break;
					case WEEKLY:
						// In case of weekly repetition, the user can choose a frequency and the days on the week 
						mRepetitionLayout.setVisibility(View.VISIBLE);
						mFrequencyLayout.setVisibility(View.VISIBLE);
						mWeekLayout.setVisibility(View.VISIBLE);
						// TODO Set the frequency unity to weeks
						mVFreqUnit.setText(R.string.week);						
						break;
					case MONTHLY:
						// TODO Utility of monthly repetition? Every 1st Monday of the month? in interaction with the start date
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
		
		// Query the DB through a contentProvider, though use a contentResolver
		ContentResolver cr = getContentResolver();
		ContentValues values=new ContentValues();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		/* TODO to adapt to activity URI
		values.put(MyGoals.Goals.COLUMN_NAME_TITLE, mVGoalTitle.getText().toString());
		values.put(MyGoals.Goals.COLUMN_NAME_DESC, mVGoalDesc.getText().toString());

		//Dates are stored as UTC date strings ("YYYY-MM-DD HH:mmZ")
		values.put(MyGoals.Goals.COLUMN_NAME_START_DATE, sdf.format(mStartDate));
		values.put(MyGoals.Goals.COLUMN_NAME_TARGET_DATE, sdf.format(mEndDate));

		values.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD, ((EditText)findViewById(R.id.t_goal_hours)).getText().toString());
		values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, 0);
		
		cr.insert(MyGoals.Goals.CONTENT_URI, values);
		*/
	}

	private void fillWithDefaultValues() {
		Log.d(TAG,"fillWithDefaultValues method");				
		mStartDate=new Date();
		mStartTime=new Date();
		mEndDate=new Date();
		
		mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
		mVStartTime.setText(SimpleDateFormat.getTimeInstance().format(mStartTime));
		mVEndDate.setText(SimpleDateFormat.getDateInstance().format(mEndDate));
		
		// TODO Get & set the Goal Title
		// mVGoalTitle.setText(text);
		
		// TODO Default spinner value and panes' visibility
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
	public Date getCurrentDate() {
		return mCurrentDate;
	}

	@Override
	public TextView getCurrentDateTextView() {
		return mCurrentDateTextView;
	}
	
}
