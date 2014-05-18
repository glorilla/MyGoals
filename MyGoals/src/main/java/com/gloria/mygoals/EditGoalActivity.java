package com.gloria.mygoals;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourmob.colorpicker.ColorPickerDialog;
import com.fourmob.colorpicker.ColorPickerSwatch.OnColorSelectedListener;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



//import android.app.TimePickerDialog;

public class EditGoalActivity extends FragmentActivity implements usesDatePickerDialogInterface, DatePickerDialog.OnDateSetListener {
	// For log purpose
	private static final  String TAG = "EditGoalActivity"; 
	
	// Intent extra parameters' interface
	public static final String EXTRA_KEY_MODE = "mode";
	public static final String EXTRA_KEY_ID = "id";

    public static enum Mode {NEW, EDIT};
	
	// activity state
	private Mode mMode=Mode.NEW;
	private int mGoalId=0;

	// form views
	EditText mVGoalTitle;
	EditText mVGoalDesc;
	EditText mVGoalWorkload;
	TextView mVStartDate;
	TextView mVEndDate;
	Button mSubmitBtn;
    ImageView mVColorPicker;
	
	// form values
	private Date mStartDate;
	private Date mEndDate;
    private int mColor;
	
	// for DatePickerDialog purpose
	private Date mCurrentDate;
	private TextView mCurrentDateTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate method");			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_edit);
		setTitle(getResources().getText(R.string.my_goal));
		
		// Set the action bar
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    getActionBar().setDisplayHomeAsUpEnabled(true);
        }		
		
		// Set the mode and the Goal id thanks to the intent extra values	
		mMode = (Mode)getIntent().getExtras().get(EXTRA_KEY_MODE);
		mGoalId = getIntent().getIntExtra(EXTRA_KEY_ID,0);

		// Init the views' values and listeners
        initViews();
	}

	private void initViews() {
		Log.d(TAG,"initViews method");			

		// initialize the spinners
        // TODO To remove as replaced by ColorPickerDialog
        // initCategorySpinner();

        // Get the layout's views
        mVGoalTitle = (EditText) findViewById(R.id.t_goal_detail_title);
        mVGoalDesc = (EditText) findViewById(R.id.t_goal_detail_description);        
		mVStartDate = (TextView) findViewById(R.id.t_pick_start_date);
		mVEndDate = (TextView) findViewById(R.id.t_pick_end_date);
		mVGoalWorkload = (EditText)findViewById(R.id.t_goal_hours);
        mVColorPicker = (ImageView)findViewById(R.id.i_color);
		mSubmitBtn = (Button) findViewById(R.id.btn_goal_detail_submit);

		// Set the submit button label and init the fields values
		switch (mMode) {
			case NEW:	// Set the label "Create" on the submit button
				mSubmitBtn.setText(R.string.create);
				fillWithDefaultValues();
				break;
			case EDIT:	// Set the label "Modify" on the submit button
				mSubmitBtn.setText(R.string.modify);				
				fillWithGoalValues(mGoalId);
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
						updateInDB(mGoalId);
						break;
					}	
					EditGoalActivity.this.finish();		// quit the activity
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

        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();

        colorPickerDialog.initialize(R.id.l_goal_detail_title, new int[] {0xffffefd5, 0xfffdfd96, 0xfff5f5dc, 0xfff2f3f4, 0xffe9ffdb, 0xffe0ffff}, mColor, 3, 2);
        colorPickerDialog.setOnColorSelectedListener(new OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                //Toast.makeText(MainActivity.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
                mColor=color;
                View rootLayout = findViewById(R.id.root_layout);
                rootLayout.setBackgroundColor(mColor);
            }
        });

        mVColorPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
            }
        });



    }


/*
	private void initCategorySpinner() {
		Log.d(TAG,"initCategorySpinner method");				
        // TODO implement a custom spinner with entries (text + image) in the drop down list; but displaying only the image selected  
        // Category Spinner
       Spinner spinner = (Spinner) findViewById(R.id.sp_goal_detail_category);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.category_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
	}*/

	private boolean validateForm() {
		//Log.d(TAG,"validateForm method");
		/* TODO To implement the form validation before submitting
		 	- are all mandatory fields filled ?
		 	- is start date < end date ?
		 	new AlertDialog.Builder(this)
	    .setTitle("Delete entry")
	    .setMessage("Are you sure you want to delete this goal and all its tasks and activities?")
		 */

        if (    (mVGoalTitle.getText().toString().isEmpty())
                || (mVGoalDesc.getText().toString().isEmpty())
                || (mVGoalWorkload.getText().toString().isEmpty())
           )
        {
            Log.d(TAG,"Error validateForm method: Title Or Desc Or WorkLoad is empty");

            // I don't know how to MAKE A DIALOG BOX with just a single button !
            /*new AlertDialog.Builder(this)
                    .setTitle("Error : Incomplete Form!")
                    .setMessage("Please check your informations and complete the forms.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                      })
                    )
                    .setIcon(R.drawable.alert_warning)
                    .show();*/
            return false;
        }

        Log.d(TAG,"validateForm method: Ok!");
		return true;
	}

	private void insertInDB() {
		Log.d(TAG,"insertInDB method");
		
		// Query the DB through a contentProvider, though use a contentResolver
		ContentResolver cr = getContentResolver();
		ContentValues values=new ContentValues();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		values.put(MyGoals.Goals.COLUMN_NAME_TITLE, mVGoalTitle.getText().toString());
		values.put(MyGoals.Goals.COLUMN_NAME_DESC, mVGoalDesc.getText().toString());

		//Dates are stored as UTC date strings ("YYYY-MM-DD HH:mmZ")
		values.put(MyGoals.Goals.COLUMN_NAME_START_DATE, sdf.format(mStartDate));
		values.put(MyGoals.Goals.COLUMN_NAME_TARGET_DATE, sdf.format(mEndDate));

		values.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD, ((EditText)findViewById(R.id.t_goal_hours)).getText().toString());
		values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, 0);
        values.put(MyGoals.Goals.COLUMN_NAME_COLOR, mColor);
		
		cr.insert(MyGoals.Goals.CONTENT_URI, values);
	}	

	private void updateInDB(int id) {
		Log.d(TAG,"fillWithGoalValues method");				
		
		// Query the DB through a contentProvider, though use a contentResolver
		ContentResolver cr = getContentResolver();
		ContentValues values=new ContentValues();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		values.put(MyGoals.Goals.COLUMN_NAME_TITLE, mVGoalTitle.getText().toString());
		values.put(MyGoals.Goals.COLUMN_NAME_DESC, mVGoalDesc.getText().toString());
		
		//Dates are stored as UTC date strings ("YYYY-MM-DD HH:mmZ")
		values.put(MyGoals.Goals.COLUMN_NAME_START_DATE, sdf.format(mStartDate));
		values.put(MyGoals.Goals.COLUMN_NAME_TARGET_DATE, sdf.format(mEndDate));
		
		values.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD, mVGoalWorkload.getText().toString());

        values.put(MyGoals.Goals.COLUMN_NAME_COLOR, mColor);
		
		cr.update(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + id), values, null, null);
	}	
	
	private void fillWithDefaultValues() {
		Log.d(TAG,"fillWithDefaultValues method");				
		mStartDate=new Date();
		mEndDate=new Date();
        mColor=0xffffefd5;

		mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
		mVEndDate.setText(SimpleDateFormat.getDateInstance().format(mEndDate));

        View rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackgroundColor(mColor);
	}
	
	private void fillWithGoalValues(int id) {
		Log.d(TAG,"fillWithGoalValues method");				
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + id), MyGoalsProvider.GOAL_PROJECTION, null, null, null);
		Log.d(TAG,"Initialize with a cursor containing " + c.getColumnCount() + "cols and "+ c.getCount() + "rows");
		c.moveToFirst();
		if (c != null) {
			mVGoalTitle.setText(c.getString(MyGoalsProvider.GOAL_TITLE_INDEX));
			mVGoalDesc.setText(c.getString(MyGoalsProvider.GOAL_DESC_INDEX));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
			mStartDate = sdf.parse(c.getString(MyGoalsProvider.GOAL_START_DATE_INDEX), new ParsePosition(0));
			mEndDate = sdf.parse(c.getString(MyGoalsProvider.GOAL_TARGET_DATE_INDEX), new ParsePosition(0));
			
			mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
			mVEndDate.setText(SimpleDateFormat.getDateInstance().format(mEndDate));
			
			mVGoalWorkload.setText(""+c.getInt(MyGoalsProvider.GOAL_WORKLOAD_INDEX));
            mColor = c.getInt(MyGoalsProvider.GOAL_COLOR_INDEX);
		}
        View rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackgroundColor(mColor);
	}

	public void showDatePickerDialog() {
		Log.d(TAG,"showDatePickerDialog method");

        //GregorianCalendar c = new GregorianCalendar();

        final Calendar c = Calendar.getInstance();
        // Use the current date as the default date in the picker
        if (mCurrentDate != null) {
            c.setTime(mCurrentDate);
        }

        // DialogFragment dialog = new DatePickerFragment();
        DatePickerDialog dialog = DatePickerDialog.newInstance(this, c.get(c.YEAR), c.get(c.MONTH), c.get(c.DAY_OF_MONTH), false);

        /*dialog.setVibrate(isVibrate());
        dialog.setYearRange(1985, 2028);*/

        dialog.show(this.getSupportFragmentManager(), "datePicker");
        // dialog.show(getFragmentManager(), "datePicker");

	}


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        DatePickerCallBack(year, month, day);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,"onCreateOptionsMenu method");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_goal, menu);
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public Date getCurrentDate() {
		return mCurrentDate;
	}

/*	@Override
	public TextView getCurrentDateTextView() {
		return mCurrentDateTextView;
	} */

	@Override
	public void DatePickerCallBack(int year, int month, int day) {
		if (mCurrentDateTextView == mVStartDate) {
			// Format the date to the user's locales and set the destination view
			final Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			mStartDate.setTime(c.getTimeInMillis());
			mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mCurrentDate));
		} else if (mCurrentDateTextView == mVEndDate) {
			// Format the date to the user's locales and set the destination view
			final Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			mEndDate.setTime(c.getTimeInMillis());
			mVEndDate.setText(SimpleDateFormat.getDateInstance().format(mCurrentDate));			
		} else {
			Log.w(TAG,"Setting a date in this DatePicker dialog should update a View parameter");			
		}
	}


	
}
