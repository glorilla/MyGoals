package com.gloria.mygoals;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EditGoalActivity extends Activity {

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
        
        // Category Spinner
        Spinner spinner = (Spinner) findViewById(R.id.sp_goal_detail_category);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.category_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_goal, menu);
		return true;
	}

}
