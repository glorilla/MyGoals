package com.gloria.mygoals;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NewGoalActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goal_detail);
        
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.		
		    // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        	
		    // If your minSdkVersion is 11 or higher, instead use:
		    getActionBar().setDisplayHomeAsUpEnabled(true);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_goal, menu);
		return true;
	}

}
