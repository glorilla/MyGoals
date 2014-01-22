package com.gloria.mygoals;

import com.gloria.mygoals.dummy.DummyData;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class GoalListFragment extends Fragment {
	private View root_view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Return the View built from the layout
		root_view = inflater.inflate(R.layout.activity_goal_list, container, false);
		return root_view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	if (root_view != null) {
    		 // get the listview in the view hierarchy
    		ListView lv= (ListView)root_view.findViewById(R.id.lv_goals);
    	
		    // create the list mapping
		    String[] from = new String[] {"title", "desc", "status", "end_date"};
		    int[] to = new int[] { R.id.t_goal_title, R.id.t_goal_desc, R.id.t_goal_status, R.id.t_goal_end_date };
		
		    // fill in the goal list layout
		    //TODO Create a custom adapter to display Goal in the list of goals with category icon, colors ...
		    SimpleAdapter adapter = new SimpleAdapter(getActivity(), DummyData.getGoalsData(), R.layout.goal_item, from, to);
		    lv.setAdapter(adapter);
		    
		    // Define action when clicking on a Goal item 
		    lv.setOnItemClickListener(new OnItemClickListener() {
		    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.v("DEBUG", "Item "+id+" has been clicked");	
		    	}
		    });
    	}
    }
    
	/* TODO Manage the action bar in fragments
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.goal_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_new_goal:
	            openNewGoalActivity();
	            return true;
	        case R.id.action_settings:
	            openSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void openNewGoalActivity() {
	    Intent intent = new Intent(this, NewGoalActivity.class);
	    startActivity(intent);
	}
	
	private void openSettings() {
		
	}*/
	
}
