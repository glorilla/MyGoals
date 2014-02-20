package com.gloria.mygoals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.gloria.mygoals.dummy.DummyData;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
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
import android.widget.TextView;;

public class GoalDetailFragment extends Fragment {
	// For log purpose
	private static final  String TAG = "GoalDetailFragment"; 
	
	private View root_view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate method");			
        super.onCreate(savedInstanceState);
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView method");					
		// Return the View built from the layout
		root_view = inflater.inflate(R.layout.goal_detail, container, false);
		Intent i = getActivity().getIntent();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
		
		((TextView)root_view.findViewById(R.id.t_goal_detail_title)).setText(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_TITLE));
		((TextView)root_view.findViewById(R.id.t_goal_detail_description)).setText(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_DESC));
		
		try {
			Date startDate = sdf.parse(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_START_DATE));
			((TextView)root_view.findViewById(R.id.t_goal_start_date)).setText(SimpleDateFormat.getDateInstance().format(startDate));
			
			Date endDate = sdf.parse(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_TARGET_DATE));
			((TextView)root_view.findViewById(R.id.t_goal_end_date)).setText(SimpleDateFormat.getDateInstance().format(endDate));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		((TextView)root_view.findViewById(R.id.t_goal_hours)).setText("" + i.getIntExtra(ViewGoalActivity.EXTRA_KEY_WORKLOAD, 0) + getResources().getString(R.string.goal_hours));
		
		return root_view;
	}	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG,"onActivityCreated method");	      	
    	super.onActivityCreated(savedInstanceState);
  	}

    @Override
    public void onStart() {
		Log.d(TAG,"onStart method");
		super.onStart();
    }

}
