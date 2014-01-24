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

public class EventListFragment extends Fragment {
	private View root_view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Return the View built from the layout
		root_view = inflater.inflate(R.layout.event_list, container, false);
		return root_view;
	}	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        ListView lv= (ListView)root_view.findViewById(R.id.lv_events); // get the listview in the view hierarchy
        
        // create the list mapping
        String[] from = new String[] {"date", "task", "activity", "feedback"};
        int[] to = new int[] { R.id.t_event_date, R.id.t_event_title, R.id.t_event_goal, R.id.t_progress};
 
        // fill in the goal list layout
        //TODO Create a custom adapter to display Event in the list of events with category color, checkbox ...
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), DummyData.getEventsData(), R.layout.event_item, from, to);
        lv.setAdapter(adapter);
        
        // Define action when clicking on a Goal item 
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Log.v("DEBUG", "Item "+id+" has been clicked");	
        	}
        });
	}
	
}
