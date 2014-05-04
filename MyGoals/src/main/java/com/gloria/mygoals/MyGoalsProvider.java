/**
 * 
 */
package com.gloria.mygoals;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * @author Guillaume
 *
 */
public class MyGoalsProvider extends ContentProvider {
    // Used for debugging and logging
    private static final String TAG = "MyGoalsProvider";

    /**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "mygoals.db";
    private static final int DATABASE_VERSION = 10;

    /**
     * Projection maps used to select columns from the database
     */
    private static HashMap<String, String> sGoalsProjectionMap;
    private static HashMap<String, String> sTasksProjectionMap;
    
    /* TODO Activities and tasks implementation
    private static HashMap<String, String> sEventsProjectionMap;
     */

    /**
     * Standard projection for the interesting columns of a goal.
     * 
     */
    public static final String[] GOAL_PROJECTION = new String[] {
            MyGoals.Goals._ID,               	// Projection position 0, the goal's id
            MyGoals.Goals.COLUMN_NAME_TITLE, 	// Projection position 1, the goal's title
            MyGoals.Goals.COLUMN_NAME_DESC,   	// Projection position 2, the goal's description
            MyGoals.Goals.COLUMN_NAME_START_DATE, 	// Projection position 3, the goal's start date
            MyGoals.Goals.COLUMN_NAME_TARGET_DATE,  // Projection position 4, the goal's target date
            MyGoals.Goals.COLUMN_NAME_WORKLOAD,	// Projection position 5, the goal's workload
            MyGoals.Goals.COLUMN_NAME_PROGRESS,	// Projection position 6, the goal's progress
            MyGoals.Goals.COLUMN_NAME_COLOR	// Projection position 6, the goal's background color
    };
    
    /*
     * Indexes of the columns in the projection 
     */
	public static final int GOAL_ID_INDEX = 0;
    public static final int GOAL_TITLE_INDEX = 1;
    public static final int GOAL_DESC_INDEX = 2;
    public static final int GOAL_START_DATE_INDEX = 3;
    public static final int GOAL_TARGET_DATE_INDEX = 4;
    public static final int GOAL_WORKLOAD_INDEX = 5;
    public static final int GOAL_PROGRESS_INDEX = 6;
    public static final int GOAL_COLOR_INDEX = 7;
         
    /**
     * Standard projection for the interesting columns of an activity.
     * 
     */
    public static final String[] ACTIVITY_PROJECTION = new String[] {
            MyGoals.Activities._ID,               	// Projection position 0, the activity's id
            MyGoals.Activities.COLUMN_NAME_TITLE, 	// Projection position 1, the activity's title
            MyGoals.Activities.COLUMN_NAME_DESC,   	// Projection position 2, the activity's description
            MyGoals.Activities.COLUMN_NAME_START_DATE, 	// Projection position 3, the activity's start date
            MyGoals.Activities.COLUMN_NAME_END_DATE,  	// Projection position 4, the activity's end date
            MyGoals.Activities.COLUMN_NAME_DURATION,	// Projection position 5, the activity's task duration
            MyGoals.Activities.COLUMN_NAME_REPETITION,	// Projection position 6, the activity's repetition
            MyGoals.Activities.COLUMN_NAME_OCCURRENCE,	// Projection position 7, the activity's occurrence
            MyGoals.Activities.COLUMN_NAME_WEEKDAYS,	// Projection position 8, the activity's weekdays
            MyGoals.Activities.COLUMN_NAME_NB_TASKS,	// Projection position 9, the activity's nb tasks
            MyGoals.Activities.COLUMN_NAME_PROGRESS,	// Projection position 10, the activity's progress (nb tasks done)
            //MyGoals.Activities.COLUMN_NAME_GOAL_COLOR,	// Projection position 11, the activity's background color
            MyGoals.Activities.COLUMN_NAME_RRULE	// Projection position 11, the activity's recurrence rule
    };
    
    /*
     * Indexes of the columns in the projection 
     */
	public static final int ACTIVITY_ID_INDEX = 0;
    public static final int ACTIVITY_TITLE_INDEX = 1;
    public static final int ACTIVITY_DESC_INDEX = 2;
    public static final int ACTIVITY_START_DATE_INDEX = 3;
    public static final int ACTIVITY_END_DATE_INDEX = 4;
    public static final int ACTIVITY_DURATION_INDEX = 5;
    public static final int ACTIVITY_REPETITION_INDEX = 6;
    public static final int ACTIVITY_OCCURRENCE_INDEX = 7;
    public static final int ACTIVITY_WEEKDAYS_INDEX = 8;
    public static final int ACTIVITY_NB_TASKS_INDEX = 9;
    public static final int ACTIVITY_PROGRESS_INDEX = 10;
    //public static final int ACTIVITY_COLOR_INDEX = 11;
    public static final int ACTIVITY_RRULE_INDEX = 11;


    /**
     * Standard projection for the interesting columns of a task.
     */ 
    public static final String[] TASK_PROJECTION = new String[] {
            MyGoals.Tasks._ID,               	// Projection position 0, the task's id
            MyGoals.Tasks.COLUMN_NAME_TITLE, 	// Projection position 1, the task's title
            MyGoals.Tasks.COLUMN_NAME_GOAL_ID,   	// Projection position 2, the task's goal id
            MyGoals.Tasks.COLUMN_NAME_GOAL_TITLE, 	// Projection position 3, the task's title            
            MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID, 	// Projection position 4, the task's activity id
            MyGoals.Tasks.COLUMN_NAME_DUE_DATE,  	// Projection position 5, the task's due date
            MyGoals.Tasks.COLUMN_NAME_START_DATE,	// Projection position 6, the task's start time
            MyGoals.Tasks.COLUMN_NAME_DONE_DATE,	// Projection position 7, the task's done date
            MyGoals.Tasks.COLUMN_NAME_DONE,		// Projection position 8, the task's done boolean
            MyGoals.Tasks.COLUMN_NAME_STATUS,	// Projection position 9, the task's status
            MyGoals.Tasks.COLUMN_NAME_GOAL_COLOR	// Projection position 10, the task's background color
    };
     
    /*
     * Indexes of the columns in the projection 
    */ 
	public static final int TASK_ID_INDEX = 0;
    public static final int TASK_TITLE_INDEX = 1;
    public static final int TASK_GOAL_ID_INDEX = 2;
    public static final int TASK_GOAL_ID_TITLE = 3;    
    public static final int TASK_ACTIVITY_ID_INDEX = 4;
    public static final int TASK_DUE_DATE_INDEX = 5;
    public static final int TASK_START_DATE_INDEX = 6;
    public static final int TASK_DONE_DATE_INDEX = 7;
    public static final int TASK_DONE_INDEX = 8;
    public static final int TASK_STATUS_INDEX = 9;
    public static final int TASK_COLOR_INDEX = 10;
    
    /*
     * Constants used by the Uri matcher to choose an action based on the pattern
     * of the incoming URI
     */
    // The incoming URI matches the Goals URI pattern
    private static final int GOALS = 1;

    // The incoming URI matches the Goal ID URI pattern
    private static final int GOAL_ID = 2;

    // The incoming URI matches the goal's activities URI pattern
    private static final int ACTIVITIES = 3;
    
    // The incoming URI matches the activity ID URI pattern
    private static final int ACTIVITY_ID = 4;
    
    // The incoming URI matches the goal's tasks URI pattern
    private static final int TASKS = 5;    

    // The incoming URI matches the goal's tasks URI pattern
    private static final int TASK_ID = 6;        
    
    /**
     * A UriMatcher instance
     */
    private static final UriMatcher sUriMatcher;

    // Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;


    /**
     * A block that instantiates and sets static objects
     */
    static {

        /*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add a pattern that routes URIs terminated with "goals" to a GOALS operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "goals", GOALS);

        // Add a pattern that routes URIs terminated with "goals" plus an integer, to a goal ID operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "goals/#", GOAL_ID);
        
        // Add a pattern that routes URIs terminated with "activities?goal_id=#" to an activity operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "activities", ACTIVITIES);

        // Add a pattern that routes URIs terminated with "activities/#" to an activity operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "activities/#", ACTIVITY_ID);

        // Add a pattern that routes URIs terminated with "tasks" to an activity operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "tasks", TASKS);

        // Add a pattern that routes URIs terminated with "tasks" to an activity operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "tasks/#", TASK_ID);        
        
        /*
         * Creates and initializes a projection map for the junction with the goal table
         */        
        sTasksProjectionMap = new HashMap<String, String>();

        // Maps the string "_id" to the column name "task._id"
        sTasksProjectionMap.put(MyGoals.Tasks._ID, MyGoals.Tasks.TABLE_NAME+"."+MyGoals.Tasks._ID);
        // Maps the string "title" to the column name "task.title"        
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_TITLE, MyGoals.Tasks.TABLE_NAME + "." + MyGoals.Tasks.COLUMN_NAME_TITLE);
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_GOAL_ID,  MyGoals.Tasks.COLUMN_NAME_GOAL_ID);
        // Maps the string "goal_title" to the column name "goal.title AS goal_title"
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_GOAL_TITLE, MyGoals.Goals.TABLE_NAME+"."+MyGoals.Goals.COLUMN_NAME_TITLE + " AS " + MyGoals.Tasks.COLUMN_NAME_GOAL_TITLE);      
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID, MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID);	
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_START_DATE, MyGoals.Tasks.TABLE_NAME + "." + MyGoals.Tasks.COLUMN_NAME_START_DATE);
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_DUE_DATE, MyGoals.Tasks.COLUMN_NAME_DUE_DATE);
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_DONE_DATE, MyGoals.Tasks.COLUMN_NAME_DONE_DATE);
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_DONE,	MyGoals.Tasks.COLUMN_NAME_DONE);	
		sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_STATUS, MyGoals.Tasks.COLUMN_NAME_STATUS);
        // Maps the string "goal_color" to the column name "goal.color AS goal_color"
        sTasksProjectionMap.put(MyGoals.Tasks.COLUMN_NAME_GOAL_COLOR, MyGoals.Goals.TABLE_NAME+"."+MyGoals.Goals.COLUMN_NAME_COLOR + " AS " + MyGoals.Tasks.COLUMN_NAME_GOAL_COLOR);

        /*
         * Creates and initializes a projection map that returns all columns
         */

        // Creates a new projection map instance. The map returns a column name
        // given a string. The two are usually equal.
        sGoalsProjectionMap = new HashMap<String, String>();

        // Maps the string "_ID" to the column name "_ID"
        sGoalsProjectionMap.put(MyGoals.Goals._ID, MyGoals.Goals._ID);

        // Maps "title" to "title"
        sGoalsProjectionMap.put(MyGoals.Goals.COLUMN_NAME_TITLE, MyGoals.Goals.COLUMN_NAME_TITLE);

        // Maps "description" to "description"
        sGoalsProjectionMap.put(MyGoals.Goals.COLUMN_NAME_DESC, MyGoals.Goals.COLUMN_NAME_DESC);

        // Maps "start date" to "start date"
        sGoalsProjectionMap.put(MyGoals.Goals.COLUMN_NAME_START_DATE,
        		MyGoals.Goals.COLUMN_NAME_START_DATE);

        // Maps "target date" to "target date"
        sGoalsProjectionMap.put(MyGoals.Goals.COLUMN_NAME_TARGET_DATE,
        		MyGoals.Goals.COLUMN_NAME_TARGET_DATE);

        // Maps "workload" to "workload"
        sGoalsProjectionMap.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD,
        		MyGoals.Goals.COLUMN_NAME_WORKLOAD);

        // Maps "progress" to "progress"
        sGoalsProjectionMap.put(MyGoals.Goals.COLUMN_NAME_PROGRESS,
        		MyGoals.Goals.COLUMN_NAME_PROGRESS);

        // Maps "color" to "color"
        sGoalsProjectionMap.put(MyGoals.Goals.COLUMN_NAME_COLOR,
                MyGoals.Goals.COLUMN_NAME_COLOR);
    }

    /**
    *
    * This class helps open, create, and upgrade the database file. Set to package visibility
    * for testing purposes.
    */
   static class DatabaseHelper extends SQLiteOpenHelper {

       DatabaseHelper(Context context) {

           // calls the super constructor, requesting the default cursor factory.
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
       }

       /**
        *
        * Creates the underlying database with table name and column names taken from the
        * NotePad class.
        */
       @Override
       public void onCreate(SQLiteDatabase db) {
    	   // Table Goals
           db.execSQL("CREATE TABLE " + MyGoals.Goals.TABLE_NAME + " ("
                   + MyGoals.Goals._ID + " INTEGER PRIMARY KEY,"
                   + MyGoals.Goals.COLUMN_NAME_TITLE + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_DESC + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_START_DATE + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_TARGET_DATE + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_WORKLOAD + " INTEGER,"
                   + MyGoals.Goals.COLUMN_NAME_PROGRESS + " INTEGER,"
                   + MyGoals.Goals.COLUMN_NAME_COLOR + " INTEGER"
                   + ");");

    	   // Table Activity
           db.execSQL("CREATE TABLE " + MyGoals.Activities.TABLE_NAME + " ("
                   + MyGoals.Activities._ID + " INTEGER PRIMARY KEY,"
                   + MyGoals.Activities.COLUMN_NAME_TITLE + " TEXT,"
                   + MyGoals.Activities.COLUMN_NAME_GOAL_ID + " INTEGER,"
                   + MyGoals.Activities.COLUMN_NAME_DESC + " TEXT,"
                   + MyGoals.Activities.COLUMN_NAME_START_DATE + " TEXT,"
                   + MyGoals.Activities.COLUMN_NAME_END_DATE + " TEXT,"
                   + MyGoals.Activities.COLUMN_NAME_DURATION + " TEXT,"
                   + MyGoals.Activities.COLUMN_NAME_REPETITION + " INTEGER,"
                   + MyGoals.Activities.COLUMN_NAME_OCCURRENCE + " INTEGER,"
                   + MyGoals.Activities.COLUMN_NAME_WEEKDAYS + " INTEGER,"
                   + MyGoals.Activities.COLUMN_NAME_NB_TASKS + " INTEGER,"
                   + MyGoals.Activities.COLUMN_NAME_PROGRESS + " INTEGER,"
                   + MyGoals.Activities.COLUMN_NAME_RRULE + " TEXT"
                   + ");");

    	   // Table Task
           db.execSQL("CREATE TABLE " + MyGoals.Tasks.TABLE_NAME + " ("
                   + MyGoals.Tasks._ID + " INTEGER PRIMARY KEY,"
                   + MyGoals.Tasks.COLUMN_NAME_TITLE + " TEXT,"
                   + MyGoals.Tasks.COLUMN_NAME_GOAL_ID + " INTEGER,"
                   + MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID + " INTEGER,"
                   + MyGoals.Tasks.COLUMN_NAME_DUE_DATE + " TEXT,"
                   + MyGoals.Tasks.COLUMN_NAME_START_DATE + " TEXT,"
                   + MyGoals.Tasks.COLUMN_NAME_DONE_DATE + " TEXT,"
                   + MyGoals.Tasks.COLUMN_NAME_DONE + " TEXT,"
                   + MyGoals.Tasks.COLUMN_NAME_STATUS + " INTEGER"
                   // TODO field feedback
                   + ");");

    	   // Table Repetition
           /*db.execSQL("CREATE TABLE " + MyGoals.Repetition.TABLE_NAME + " ("
                   + MyGoals.Repetition._ID + " INTEGER PRIMARY KEY,"
                   + MyGoals.Repetition.COLUMN_NAME_NAME + " TEXT"
                   + ");");*/
           
    	   // Table Status
           db.execSQL("CREATE TABLE " + MyGoals.Status.TABLE_NAME + " ("
                   + MyGoals.Status._ID + " INTEGER PRIMARY KEY,"
                   + MyGoals.Status.COLUMN_NAME_NAME + " TEXT"
                   + ");");

    	   // Table Category
           db.execSQL("CREATE TABLE " + MyGoals.Category.TABLE_NAME + " ("
                   + MyGoals.Category._ID + " INTEGER PRIMARY KEY,"
                   + MyGoals.Category.COLUMN_NAME_NAME + " TEXT,"
                   + MyGoals.Category.COLUMN_NAME_COLOR + " INTEGER"                   
                   + ");");

       }

       /**
        *
        * Demonstrates that the provider must consider what happens when the
        * underlying datastore is changed. In this sample, the database is upgraded the database
        * by destroying the existing data.
        * A real application should upgrade the database in place.
        */
       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

           // Logs that the database is being upgraded
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");

           // Kills the table and existing data
           db.execSQL("DROP TABLE IF EXISTS " + MyGoals.Category.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + MyGoals.Repetition.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + MyGoals.Status.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + MyGoals.Activities.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + MyGoals.Tasks.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + MyGoals.Goals.TABLE_NAME);

           // Recreates the database with a new version
           onCreate(db);
       }
   }

   /**
    *
    * Initializes the provider by creating a new DatabaseHelper. onCreate() is called
    * automatically when Android creates the provider in response to a resolver request from a
    * client.
    */
   @Override
   public boolean onCreate() {

       // Creates a new helper object. Note that the database itself isn't opened until
       // something tries to access it, and it's only created if it doesn't already exist.
       mOpenHelper = new DatabaseHelper(getContext());

       // Assumes that any failures will be reported by a thrown exception.
       return true;
   }

	/**
    * This method is called when a client calls
    * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)}.
    * Queries the database and returns a cursor containing the results.
    *
    * @return A cursor containing the results of the query. The cursor exists but is empty if
    * the query returns no results or an exception occurs.
    * @throws IllegalArgumentException if the incoming URI pattern is invalid.
    */

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	       // Constructs a new query builder and sets its table name
	       SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

	       String orderBy="";
	       /**
	        * Choose the projection and adjust the "where" clause based on URI pattern-matching.
	        */
	       switch (sUriMatcher.match(uri)) {
	           // If the incoming URI is for goals, chooses the Goals projection
	           case GOALS:
	    	       qb.setTables( MyGoals.Goals.TABLE_NAME);
	               qb.setProjectionMap(sGoalsProjectionMap); // <"a","b as a"> used for 'select "b as a"'
		           orderBy = MyGoals.Goals.DEFAULT_SORT_ORDER;	               
	               break;

	           /* If the incoming URI is for a single goal identified by its ID, chooses the
	            * goal ID projection, and appends "_ID = <goalID>" to the where clause, so that
	            * it selects that single goal
	            */
	           case GOAL_ID:
	    	       qb.setTables( MyGoals.Goals.TABLE_NAME);	        	   
	               qb.setProjectionMap(sGoalsProjectionMap);
	               qb.appendWhere(
	                   MyGoals.Goals._ID + "=" +   // the name of the ID column
	                   // the position of the goal ID itself in the incoming URI
	                   uri.getPathSegments().get(MyGoals.Goals.GOAL_ID_PATH_POSITION));
		           orderBy = MyGoals.Goals.DEFAULT_SORT_ORDER;	               
	               break;
	           
	           case ACTIVITIES:
	        	   // TODO to replace the immediate string by a constant
	        	   String goal_id = uri.getQueryParameter("goal_id");
	        	   // TODO Throw an exception when goal_id is null
	    	       qb.setTables( MyGoals.Activities.TABLE_NAME);
	    	       // TODO required? qb.setProjectionMap(sActivitiesProjectionMap);
	               qb.appendWhere(MyGoals.Activities.COLUMN_NAME_GOAL_ID + "=" + goal_id);
		           orderBy = MyGoals.Activities.DEFAULT_SORT_ORDER;	               
	        	   break;
        	   
	           case ACTIVITY_ID:
	    	       qb.setTables( MyGoals.Activities.TABLE_NAME);
	    	       // TODO required? qb.setProjectionMap(sActivitiesProjectionMap);
	               qb.appendWhere(
	                   MyGoals.Activities._ID + "=" +   // the name of the ID column
	                   // the position of the activity ID in the incoming URI
	                   uri.getPathSegments().get(MyGoals.Activities.ACTIVITY_ID_PATH_POSITION));	
		           orderBy = MyGoals.Activities.DEFAULT_SORT_ORDER;
	               break;
	        	   
	           case TASKS:
	        	   // TODO to replace the immediate string by a constant
	        	   String activity_id = uri.getQueryParameter("activity_id");
	        	   if (activity_id != null) {
	        		   qb.appendWhere(MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID + "=" + activity_id + " AND ");
	        	   }
	    	       qb.setTables( MyGoals.Tasks.TABLE_NAME + ", " + MyGoals.Goals.TABLE_NAME);
	    	       // Projection map for the junction with the Goal table
	    	       qb.setProjectionMap(sTasksProjectionMap);
	    	       qb.appendWhere(MyGoals.Tasks.COLUMN_NAME_GOAL_ID + "=" + MyGoals.Goals.TABLE_NAME + "." + MyGoals.Goals._ID);
		           orderBy = MyGoals.Tasks.DEFAULT_SORT_ORDER;	   
	        	   break;
	        	   
	           case TASK_ID:
	    	       qb.setTables( MyGoals.Tasks.TABLE_NAME);
	    	       // TODO required? qb.setProjectionMap(sTasksProjectionMap);
	               qb.appendWhere(
	                   MyGoals.Tasks._ID + "=" +   // the name of the ID column
	                   // the position of the task ID in the incoming URI
	                   uri.getPathSegments().get(MyGoals.Tasks.TASK_ID_PATH_POSITION));	
		           orderBy = MyGoals.Tasks.DEFAULT_SORT_ORDER;
	               break;	        	   
	        	   
	           default:
	               // If the URI doesn't match any of the known patterns, throw an exception.
	               throw new IllegalArgumentException("Unknown URI " + uri);
	       }

	       // If a sort order is specified, use it instead of the default one
	       if (!TextUtils.isEmpty(sortOrder)) {
	           orderBy = sortOrder;
	       }

	       // Opens the database object in "read" mode, since no writes need to be done.
	       SQLiteDatabase db = mOpenHelper.getReadableDatabase();

	       /*
	        * Performs the query. If no problems occur trying to read the database, then a Cursor
	        * object is returned; otherwise, the cursor variable contains null. If no records were
	        * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
	        */
	       Cursor c = qb.query(
	           db,            // The database to query
	           projection,    // The columns to return from the query
	           selection,     // The columns for the where clause
	           selectionArgs, // The values for the where clause
	           null,          // don't group the rows
	           null,          // don't filter by row groups
	           orderBy        // The sort order
	       );

	       // Tells the Cursor what URI to watch, so it knows when its source data changes
	       if (null != c) c.setNotificationUri(getContext().getContentResolver(), uri);

           return c;
	   }
   
   /**
    * This is called when a client calls {@link android.content.ContentResolver#getType(Uri)}.
    * Returns the MIME data type of the URI given as a parameter.
    *
    * @param uri The URI whose MIME type is desired.
    * @return The MIME type of the URI.
    * @throws IllegalArgumentException if the incoming URI pattern is invalid.
    */
	@Override
	public String getType(Uri uri) {

	       /**
	        * Chooses the MIME type based on the incoming URI pattern
	        */
	       switch (sUriMatcher.match(uri)) {

	           // If the pattern is for goals, returns the general content type.
	           case GOALS:
	               return MyGoals.Goals.CONTENT_TYPE;
	           // If the pattern is for goal IDs, returns the goal ID content type.
	           case GOAL_ID:
	               return MyGoals.Goals.CONTENT_ITEM_TYPE;
	           case ACTIVITIES:
	               return MyGoals.Activities.CONTENT_TYPE;	        	   
	           case ACTIVITY_ID:
	               return MyGoals.Activities.CONTENT_ITEM_TYPE;	  
	           case TASKS:
	               return MyGoals.Tasks.CONTENT_TYPE;
	           case TASK_ID:
	        	   return MyGoals.Tasks.CONTENT_ITEM_TYPE;
	           // If the URI pattern doesn't match any permitted patterns, throws an exception.
	           default:
	               throw new IllegalArgumentException("Unknown URI " + uri);
	       }
	}

    /**
     * This is called when a client calls
     * {@link android.content.ContentResolver#insert(Uri, ContentValues)}.
     * Inserts a new row into the database. This method sets up default values for any
     * columns that are not included in the incoming map.
     * If rows were inserted, then listeners are notified of the change.
     * @return The row ID of the inserted row.
     * @throws SQLException if the insertion fails.
     */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

        // Validates the incoming URI. Only the full provider URI is allowed for inserts.
        switch (sUriMatcher.match(uri))  {
        	case GOALS:
        		return insertGoal(uri, initialValues);
        	case ACTIVITIES:
        		return insertActivity(uri, initialValues);
        	case TASKS:
        		return insertTask(uri, initialValues);
        	default:	
        		throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	private Uri insertGoal(Uri uri, ContentValues initialValues) {
        // If the incoming values map is not null, uses it for the new values.
        if (initialValues == null) {
            throw new IllegalArgumentException("Values are missing to insert with URI " + uri);
        }

        // TODO If the values map doesn't contain the creation date, sets the value to the current time.
        if ( initialValues.containsKey(MyGoals.Goals.COLUMN_NAME_TITLE) == false || 
        		initialValues.containsKey(MyGoals.Goals.COLUMN_NAME_START_DATE) == false || 
        		initialValues.containsKey(MyGoals.Goals.COLUMN_NAME_TARGET_DATE) == false ||   
        		initialValues.containsKey(MyGoals.Goals.COLUMN_NAME_WORKLOAD) == false 
        		)  
        {
            // If the mandatory informations are missing, throw an exception.
            throw new IllegalArgumentException("Some mandatory values are missing to insert with URI " + uri);
        }

        // If the values map doesn't contain a decription, set it empty
        if (initialValues.containsKey(MyGoals.Goals.COLUMN_NAME_DESC) == false) {
        	initialValues.put(MyGoals.Goals.COLUMN_NAME_DESC, "");
        }

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(
        	MyGoals.Goals.TABLE_NAME,        // The table to insert into.
        	null,  							 // A hack, SQLite sets this column value to null
                                             // if values is empty.
        	initialValues                    // A map of column names, and the values to insert
                                             // into the columns.
        );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri goalUri = ContentUris.withAppendedId(MyGoals.Goals.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(goalUri, null);

            return goalUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
	}

	private Uri insertActivity(Uri uri, ContentValues initialValues) {
        // If the incoming values map is not null, uses it for the new values.
        if (initialValues == null) {
            throw new IllegalArgumentException("Values are missing to insert with URI " + uri);
        }

        // TODO Check the mandatory values expected
        if ( initialValues.containsKey(MyGoals.Activities.COLUMN_NAME_TITLE) == false || 
        		initialValues.containsKey(MyGoals.Activities.COLUMN_NAME_START_DATE) == false || 
        		initialValues.containsKey(MyGoals.Activities.COLUMN_NAME_DURATION) == false ||   
        		initialValues.containsKey(MyGoals.Activities.COLUMN_NAME_REPETITION) == false 
        		)  
        {
            // If the mandatory informations are missing, throw an exception.
            throw new IllegalArgumentException("Some mandatory values are missing to insert with URI " + uri);
        }

        // TODO Set the default values when not mandatory and not provided

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(
        	MyGoals.Activities.TABLE_NAME,        // The table to insert into.
        	null,  							 // A hack, SQLite sets this column value to null
                                             // if values is empty.
        	initialValues                    // A map of column names, and the values to insert
                                             // into the columns.
        );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri activityUri = ContentUris.withAppendedId(MyGoals.Activities.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(activityUri, null);

            return activityUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
	}

	private Uri insertTask(Uri uri, ContentValues initialValues) {

        // If the incoming values map is not null, uses it for the new values.
        if (initialValues == null) {
            throw new IllegalArgumentException("Values are missing to insert with URI " + uri);
        }

        // TODO Check the mandatory values expected
        if ( initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_TITLE) == false || 
        		initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_GOAL_ID) == false || 
        		initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID) == false ||   
        		initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_DUE_DATE) == false ||   
        		initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_START_DATE) == false )  
        {
            // If the mandatory informations are missing, throw an exception.
            throw new IllegalArgumentException("Some mandatory values are missing to insert with URI " + uri);
        }

        // TODO Set the default values when not mandatory and not provided
        if (initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_DONE_DATE) == false) {
        	initialValues.put(MyGoals.Tasks.COLUMN_NAME_DONE_DATE, "");
        }
        if (initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_DONE) == false) {
        	initialValues.put(MyGoals.Tasks.COLUMN_NAME_DONE, 0);
        }
        if (initialValues.containsKey(MyGoals.Tasks.COLUMN_NAME_STATUS) == false) {
        	initialValues.put(MyGoals.Tasks.COLUMN_NAME_STATUS, 0);
        }
        
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(
        	MyGoals.Tasks.TABLE_NAME,        // The table to insert into.
        	null,  							 // A hack, SQLite sets this column value to null
                                             // if values is empty.
        	initialValues                    // A map of column names, and the values to insert
                                             // into the columns.
        );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri taskUri = ContentUris.withAppendedId(MyGoals.Tasks.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(taskUri, null);

            return taskUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Validates the incoming URI.
        switch (sUriMatcher.match(uri))  {
        	case GOAL_ID:        		
        		return deleteGoal(uri, selection, selectionArgs);
        	case ACTIVITY_ID:
        		return deleteActivity(uri, selection, selectionArgs);
        	case TASK_ID:
        		return deleteTask(uri, selection, selectionArgs);
        	default:	
        		throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}
	
	public int deleteGoal(Uri uri, String selection, String[] selectionArgs) {
 
		if (selection == null) {
			selection = MyGoals.Goals._ID + "=" + 
			// the position of the note ID itself in the incoming URI
			uri.getPathSegments().get(MyGoals.Goals.GOAL_ID_PATH_POSITION);
		}
		
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Execute the Delete SQL command
        int nbRow = db.delete(
            	MyGoals.Goals.TABLE_NAME,    // The table to update.
        		selection, 
        		null
        );

        // Notifies observers registered against this provider that the data changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return nbRow;	
	}

	public int deleteActivity(Uri uri, String selection, String[] selectionArgs) {
 
		if (selection == null) {
			selection = MyGoals.Activities._ID + "=" + 
			// the position of the note ID itself in the incoming URI
			uri.getPathSegments().get(MyGoals.Activities.ACTIVITY_ID_PATH_POSITION);
		}
		
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Execute the Delete SQL command
        int nbRow = db.delete(
            	MyGoals.Activities.TABLE_NAME,    // The table to update.
        		selection, 
        		null
        );

        // Notifies observers registered against this provider that the data changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return nbRow;	
	}		

	public int deleteTask(Uri uri, String selection, String[] selectionArgs) {
		 
		if (selection == null) {
			selection = MyGoals.Tasks._ID + "=" + 
			// the position of the note ID itself in the incoming URI
			uri.getPathSegments().get(MyGoals.Tasks.TASK_ID_PATH_POSITION);
		}
		
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Execute the Delete SQL command
        int nbRow = db.delete(
            	MyGoals.Tasks.TABLE_NAME,    // The table to update.
        		selection, 
        		null
        );

        // Notifies observers registered against this provider that the data changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return nbRow;	
	}		
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Validates the incoming URI.
        switch (sUriMatcher.match(uri))  {
        	case GOAL_ID:        		
        		return updateGoal(uri, values, selection, selectionArgs);
        	case ACTIVITY_ID:
        		return updateActivity(uri, values, selection, selectionArgs);
        	case TASK_ID:
        		return updateTask(uri, values, selection, selectionArgs);
        	default:	
        		throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}
	
	public int updateGoal(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		if (selection == null) {
			selection = MyGoals.Goals._ID + "=" + 
			// the position of the note ID itself in the incoming URI
			uri.getPathSegments().get(MyGoals.Goals.GOAL_ID_PATH_POSITION);
		}
 	
        // If the incoming values map is null, throws an exception
        if (values == null) {
            throw new IllegalArgumentException("Values are missing to update with URI " + uri);
        }
/*
        // At least one field value must be provided for an update 	
        if ( values.containsKey(MyGoals.Goals.COLUMN_NAME_TITLE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_DESC) == false ||
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_START_DATE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_TARGET_DATE) == false ||   
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_WORKLOAD) == false ||
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_PROGRESS) == false      		
        		)  
        {
            // If the mandatory informations are missing, throws an exception.
            throw new IllegalArgumentException("At least one field value must be provided to update the DB entry");
        }
*/
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        int nbRow = db.update(
            	MyGoals.Goals.TABLE_NAME,    // The table to update.
        		values, 
        		selection, 
        		null
        );

        // Notifies observers registered against this provider that the data changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return nbRow;
	}

	public int updateActivity(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		if (selection == null) {
			selection = MyGoals.Activities._ID + "=" + 
			// the position of the note ID itself in the incoming URI
			uri.getPathSegments().get(MyGoals.Activities.ACTIVITY_ID_PATH_POSITION);
		}
 	
        // If the incoming values map is null, throws an exception
        if (values == null) {
            throw new IllegalArgumentException("Values are missing to update with URI " + uri);
        }

        /* TODO At least one field value must be provided for an update 	
        if ( values.containsKey(MyGoals.Goals.COLUMN_NAME_TITLE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_DESC) == false ||
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_START_DATE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_TARGET_DATE) == false ||   
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_WORKLOAD) == false 
        		)  
        {
            // If the mandatory informations are missing, throws an exception.
            throw new IllegalArgumentException("At least one field value must be provided to update the DB entry");
        }
         */
        
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        int nbRow = db.update(
            	MyGoals.Activities.TABLE_NAME,    // The table to update.
        		values, 
        		selection, 
        		null
        );

        // Notifies observers registered against this provider that the data changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return nbRow;
	}
	
	public int updateTask(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		if (selection == null) {
			selection = MyGoals.Tasks._ID + "=" + 
			// the position of the note ID itself in the incoming URI
			uri.getPathSegments().get(MyGoals.Tasks.TASK_ID_PATH_POSITION);
		}
 	
        // If the incoming values map is null, throws an exception
        if (values == null) {
            throw new IllegalArgumentException("Values are missing to update with URI " + uri);
        }

        /* TODO At least one field value must be provided for an update 	
        if ( values.containsKey(MyGoals.Goals.COLUMN_NAME_TITLE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_DESC) == false ||
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_START_DATE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_TARGET_DATE) == false ||   
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_WORKLOAD) == false 
        		)  
        {
            // If the mandatory informations are missing, throws an exception.
            throw new IllegalArgumentException("At least one field value must be provided to update the DB entry");
        }*/

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        int nbRow = db.update(
            	MyGoals.Tasks.TABLE_NAME,    // The table to update.
        		values, 
        		selection, 
        		null
        );

        // Notifies observers registered against this provider that the data changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return nbRow;
	}
	
}
