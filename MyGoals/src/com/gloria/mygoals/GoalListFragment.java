package com.gloria.mygoals;

import com.gloria.mygoals.dummy.DummyData;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;


public class GoalListFragment extends Fragment {
	private View root_view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Return the View built from the layout
		root_view = inflater.inflate(R.layout.goal_list, container, false);
		return root_view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    }
	
    @Override
    public void onStart() {
    	super.onStart();
        	
    	if (root_view != null) {
    		// get the listview in the view hierarchy
    		ListView lv= (ListView)root_view.findViewById(R.id.lv_goals);
    	
		    // create the list mapping
		    /*String[] from = new String[] {"title", "desc", "status", "end_date", "progress"};
		    int[] to = new int[] { R.id.t_goal_title, R.id.t_goal_desc, R.id.t_goal_status, R.id.t_goal_end_date, R.id.goal_progress };
		    */
    		
    		ContentResolver cr = getActivity().getContentResolver();
    		Uri uri = MyGoals.Goals.CONTENT_URI;
    		Cursor c = cr.query(uri, MyGoalsProvider.GOAL_PROJECTION, null, null, null);
    		
    		String[] from = new String[] {
    				MyGoals.Goals.COLUMN_NAME_TITLE, 
    				MyGoals.Goals.COLUMN_NAME_DESC,
    				MyGoals.Goals.COLUMN_NAME_TARGET_DATE
    				};
		    int[] to = new int[] { 
		    		R.id.t_goal_title, 
		    		R.id.t_goal_desc, 
		    		R.id.t_goal_end_date
		    		};
    		
		    SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.goal_item, c, from, to, 0);
		    
		    /*
		    // fill in the goal list layout
		    //TODO Create a custom adapter to display Goal in the list of goals with category icon, colors ...
		    SimpleAdapter adapter = new SimpleAdapter(getActivity(), DummyData.getGoalsData(), 
		    		R.layout.goal_item, from, to);
		    // specific Binder to represent the progress value in the progress bar
		    adapter.setViewBinder(new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) {
					if (view instanceof ProgressBar) {
                        if (data instanceof Integer) {
                            ((ProgressBar)view).setProgress(((Integer)data).intValue());
                            return true;
                        } else if (data instanceof String) {
                        	int value=Integer.parseInt((String)data);
                        	((ProgressBar)view).setProgress(value);
                        	return true;
                        } else {
                        	throw new IllegalStateException(view.getClass().getName() + " is not a" +
                                    " view that can be bounds by this SimpleAdapter using data of type " + data.getClass().getName());
                        }
                    }
					// by returning false, the SimpleAdapter natively maps data of types String, Boolean, Integer or Image  
					return false;
				}
			});
		    */
		    
		    lv.setAdapter(adapter);
		    
		    // Define action when clicking on a Goal item 
		    lv.setOnItemClickListener(new OnItemClickListener() {
		    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.d("DEBUG", "Goal "+id+" has been clicked");
					Cursor c= ((CursorAdapter)((ListView)parent).getAdapter()).getCursor();
					c.moveToPosition(position);
					int goal_id = c.getInt(MyGoalsProvider.GOAL_ID_INDEX);
					viewGoal(c, goal_id);
		    	}
		    });
    	}
    }
    
	private void viewGoal(Cursor c, int goal_id) {
	    Intent intent = new Intent(getActivity(), ViewGoalActivity.class);
	    
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_ID, goal_id);
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_TITLE, c.getString(MyGoalsProvider.GOAL_TITLE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_DESC, c.getString(MyGoalsProvider.GOAL_DESC_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_START_DATE, c.getString(MyGoalsProvider.GOAL_START_DATE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_TARGET_DATE, c.getString(MyGoalsProvider.GOAL_TARGET_DATE_INDEX));
	    intent.putExtra(ViewGoalActivity.EXTRA_KEY_WORKLOAD, c.getString(MyGoalsProvider.GOAL_WORKLOAD_INDEX));
	    
	    startActivity(intent);
	}
	
}
