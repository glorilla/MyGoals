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

public class ActivityListFragment extends Fragment {
	private View root_view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Return the View built from the layout
		root_view = inflater.inflate(R.layout.activity_list, container, false);
		return root_view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	if (root_view != null) {
    		 // get the listview in the view hierarchy
    		ListView lv= (ListView)root_view.findViewById(R.id.lv_activities);
    	
		    // create the list mapping
		    String[] from = new String[] {"title", "start_date", "end_date", "status", "progress", "effort"};
		    int[] to = new int[] { R.id.t_activity_title, R.id.t_start_date, R.id.t_end_date, R.id.t_status, R.id.t_progress, R.id.t_effort };
		
		    // fill in the goal list layout
		    //TODO Create a custom adapter to display Activity in the list of activities with category icon, colors ...
		    SimpleAdapter adapter = new SimpleAdapter(getActivity(), DummyData.getActivitiesData(), R.layout.activity_item, from, to);
		    lv.setAdapter(adapter);
		    
		    // Define action when clicking on a Goal item 
		    lv.setOnItemClickListener(new OnItemClickListener() {
		    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.v("DEBUG", "Activity "+id+" has been clicked");	
		    	}
		    });
    	}
    }
}
