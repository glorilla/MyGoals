package com.gloria.mygoals;

import java.net.URI;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import java.sql.Date;

public class EditGoalActivity extends Activity {
	
	public static final String EXTRA_KEY_MODE = "mode";
	public static final String EXTRA_KEY_ID = "id";
	public static enum Mode {NEW, EDIT};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_edit);
		
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.		
		    // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        	
		    // If your minSdkVersion is 11 or higher, instead use:
		    getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // TODO implement a custom spinner with entries (text + image) in the dropdown list; but displaying only the image selected  
        // Category Spinner
        Spinner spinner = (Spinner) findViewById(R.id.sp_goal_detail_category);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.category_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// Instantiate the form button
		Button submitBtn = (Button) findViewById(R.id.btn_goal_detail_submit);		

		// Get the intent extra values	
		Mode mode = (Mode)getIntent().getExtras().get(EXTRA_KEY_MODE);
		
		// Initialization of the fields
		// Determine if this activity has been called in mode New or Edit
		switch (mode) {
			case NEW:
				// Blank form with button "Create"
				submitBtn.setText(R.string.create);
				break;
			case EDIT:
				// Filled form with button "Modify"
				submitBtn.setText(R.string.modify);				
				// Init with the DB values corresponding to the Goal referred with the _id == id
				String id = (String)getIntent().getExtras().get(EXTRA_KEY_ID);
				initialize(id);
				break;
			default:
		}
		
		submitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// check the form values
				if (validateForm()) {
					// insert in db
					insertInDB();
					// quit the activity
					EditGoalActivity.this.finish();
				}	
			}
		});
	}

	private boolean validateForm() {
		// TODO To implement the form validation before submitting
		return true;
		//return false;
	}

	private void insertInDB() {
		// insert in db
		ContentResolver cr = getContentResolver();
		ContentValues values=new ContentValues();
		values.put(MyGoals.Goals.COLUMN_NAME_TITLE, ((EditText)findViewById(R.id.t_goal_detail_title)).getText().toString());
		values.put(MyGoals.Goals.COLUMN_NAME_DESC, ((EditText)findViewById(R.id.t_goal_detail_description)).getText().toString());
		//Date stored as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
		DatePicker datePicker = (DatePicker)findViewById(R.id.d_goal_start_date);
		Date tmpDate= new Date (datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
		values.put(MyGoals.Goals.COLUMN_NAME_START_DATE, tmpDate.toString());
		datePicker = (DatePicker)findViewById(R.id.d_goal_end_date);
		tmpDate = new Date (datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
		values.put(MyGoals.Goals.COLUMN_NAME_TARGET_DATE, tmpDate.toString());
		values.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD, ((EditText)findViewById(R.id.t_goal_hours)).getText().toString());
		values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, 0);
		
		cr.insert(MyGoals.Goals.CONTENT_URI, values);
	}	
	
	private void initialize(String id) {
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + id), null, null, null, null);
		if (c != null) {
			((EditText)findViewById(R.id.t_goal_detail_title)).setText(c.getString(MyGoalsProvider.GOAL_TITLE_INDEX));
			((EditText)findViewById(R.id.t_goal_detail_description)).setText(c.getString(MyGoalsProvider.GOAL_DESC_INDEX));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_goal, menu);
		return true;
	}

}
