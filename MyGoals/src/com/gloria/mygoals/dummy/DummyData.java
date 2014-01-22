package com.gloria.mygoals.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DummyData {

	public DummyData() {
	}
	
	public static List<HashMap<String, String>> getGoalsData() {
		List<HashMap<String, String>> goals_data = new ArrayList<HashMap<String, String>>();
	    
		HashMap<String, String> goal_map1 = new HashMap<String, String>();
	    goal_map1.put("title", "To pass PMP exam");
	    goal_map1.put("desc", "This project management certification will be a real plus on my CV");
	    goal_map1.put("status", "In progress");
	    goal_map1.put("end_date", "31/07/14");
	    goals_data.add(goal_map1);
	    
		HashMap<String, String> goal_map6 = new HashMap<String, String>();
	    goal_map6.put("title", "To go swimming");
	    goal_map6.put("desc", "I need to do some sport");
	    goal_map6.put("status", "Planned");
	    goal_map6.put("end_date", "31/12/14");
	    goals_data.add(goal_map6);
	    
		HashMap<String, String> goal_map7 = new HashMap<String, String>();
	    goal_map7.put("title", "To find a job");
	    goal_map7.put("desc", "Not an option ... unfortunately");
	    goal_map7.put("status", "In progress");
	    goal_map7.put("end_date", "31/03/14");
	    goals_data.add(goal_map7);	    

		HashMap<String, String> goal_map8 = new HashMap<String, String>();
	    goal_map8.put("title", "To change my car");
	    goal_map8.put("desc", "Quite difficult");
	    goal_map8.put("status", "In progress");
	    goal_map8.put("end_date", "31/03/14");
	    goals_data.add(goal_map8);
	    
		return goals_data;
	}

	public static List<HashMap<String, String>> getEventsData() {
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	    //"date", "task", "activity", "feedback"
	    
		HashMap<String, String> map3 = new HashMap<String, String>();
	    map3.put("task", "To write a CV");
	    map3.put("feedback", "Not an option ... unfortunately");
	    map3.put("activity", "To find a job");
	    map3.put("date", "18/01/14");
	    data.add(map3);	   
		
		HashMap<String, String> map1 = new HashMap<String, String>();
	    map1.put("task", "To Read the PMBok #5 on 12");
	    map1.put("feedback", "This lesson was OK");
	    map1.put("activity", "To Prepare the PMI exam");
	    map1.put("date", "19/01/14");
	    data.add(map1);

		HashMap<String, String> map5 = new HashMap<String, String>();
	    map5.put("task", "1km brasse + 1 km dos");
	    map5.put("feedback", "It was quite hard");
	    map5.put("activity", "To go swimming");
	    map5.put("date", "20/01/14");
	    data.add(map5);
	    
		HashMap<String, String> map6 = new HashMap<String, String>();
	    map6.put("task", "To Read the PMBok #6 on 12");
	    map6.put("feedback", "I'll need to read this one again");
	    map6.put("activity", "To Prepare the PMI exam");
	    map6.put("date", "21/01/14");
	    data.add(map6);
	    
		HashMap<String, String> map7 = new HashMap<String, String>();
	    map7.put("task", "To Train with the simulation software");
	    map7.put("feedback", "I need to do it again");
	    map7.put("activity", "To Prepare the PMI exam");
	    map7.put("date", "22/01/14");
	    data.add(map7);	    
	    
		HashMap<String, String> map2 = new HashMap<String, String>();
	    map2.put("task", "1km crawl + 1 km dos");
	    map2.put("feedback", "");
	    map2.put("activity", "To go swimming");
	    map2.put("date", "27/01/14");
	    data.add(map2);
		HashMap<String, String> map8 = new HashMap<String, String>();
	    map8.put("task", "To pass the exam");
	    map8.put("feedback", "");
	    map8.put("activity", "To Prepare the PMI exam");
	    map8.put("date", "01/03/14");
	    data.add(map8);

		HashMap<String, String> map4 = new HashMap<String, String>();
	    map4.put("task", "To meet Mr.Gates");
	    map4.put("feedback", "Quite difficult");
	    map4.put("activity", "To find a job");
	    map4.put("date", "02/03/14");
	    data.add(map4);
	    
		return data;
	}

}
