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
import android.widget.TextView;;

public class GoalDetailFragment extends Fragment {
	private View root_view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Return the View built from the layout
		root_view = inflater.inflate(R.layout.goal_detail, container, false);
		Intent i = getActivity().getIntent();
		((TextView)root_view.findViewById(R.id.t_goal_detail_title)).setText(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_TITLE));
		((TextView)root_view.findViewById(R.id.t_goal_detail_description)).setText(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_DESC));
		((TextView)root_view.findViewById(R.id.t_goal_start_date)).setText(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_START_DATE));
		((TextView)root_view.findViewById(R.id.t_goal_end_date)).setText(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_TARGET_DATE));
		((TextView)root_view.findViewById(R.id.t_goal_hours)).setText(i.getStringExtra(ViewGoalActivity.EXTRA_KEY_WORKLOAD) + R.string.goal_hours);
		return root_view;
	}	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
	}

}
