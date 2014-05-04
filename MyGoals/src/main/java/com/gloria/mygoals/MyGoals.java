package com.gloria.mygoals;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MyGoals {
    public static final String AUTHORITY = "com.gloria.mygoals";
    
    // This class cannot be instantiated
	private MyGoals() {
	}

    /**
     * Goals table contract
     */
    public static final class Goals implements BaseColumns {
        // This class cannot be instantiated
        private Goals() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "goal";

        /*
         * URI definitions
         */
        
        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */

        /**
         * Path part for the Goals URI
         */
        private static final String PATH_GOALS = "/goals";

        /**
         * Path part for the Goal ID URI
         */
        private static final String PATH_GOAL_ID = "/goals/";

        /**
         * 0-relative position of a goal ID segment in the path part of a goal ID URI
         */
        public static final int GOAL_ID_PATH_POSITION = 1;
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_GOALS);

        /**
         * The content URI base for a single goal. Callers must
         * append a numeric goal id to this Uri to retrieve a goal
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_GOAL_ID);

        /**
         * The content URI match pattern for a single goal, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_GOAL_ID + "/#");

        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of goals.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gloria.goal";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * goal.
         */
         
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gloria.goal";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /*
         * Column definitions
         */

        /**
         * Column name for the title of the goal
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_TITLE = "title";

        /**
         * Column name of the goal description
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DESC = "desc";

        /**
         * Column name for the goal start date
         * <P>Type: TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")</P>
         */
        public static final String COLUMN_NAME_START_DATE = "start_dt";

        /**
         * Column name for the goal target date
         * <P>Type: TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")</P>
         */
        public static final String COLUMN_NAME_TARGET_DATE = "target_dt";
        
        /**
         * Column name for the goal workload
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_WORKLOAD = "workload";       

        /**
         * Column name for the goal progress
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_PROGRESS = "progress";

        /**
         * Column name for the background color
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_COLOR = "color";

    }
    
    
    /**
     * Activities table contract
     */
    public static final class Activities implements BaseColumns {

        // This class cannot be instantiated
        private Activities() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "activity";

        /*
         * URI definitions
         */
        
        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */

        /**
         * Path part for the Activities URI
         */
        private static final String PATH_ACTIVITIES = "/activities";

        /**
         * Path part for the Activity ID URI
         */
        private static final String PATH_ACTIVITY_ID = "/activities/";

        /**
         * 0-relative position of a activity ID segment in the path part of an activity ID URI
         */
        public static final int ACTIVITY_ID_PATH_POSITION = 1;
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_ACTIVITIES);

        /**
         * The content URI base for a single activity. Callers must
         * append a numeric activity id to this Uri to retrieve a activity
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_ACTIVITY_ID);

        /**
         * The content URI match pattern for a single activity, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_ACTIVITY_ID + "/#");
   
        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of activities.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gloria.activity";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * activity.
         */
         
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gloria.activity";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /*
         * Column definitions
         */

        /**
         * Column name for the title of the activity
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_TITLE = "title";

        /**
         * Column name for the goal id of the activity
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_GOAL_ID = "goal_id";        
        
        /**
         * Column name of the activity description
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DESC = "desc";

        /**
         * Column name for the activity start date
         * <P>Type: TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")</P>
         */
        public static final String COLUMN_NAME_START_DATE = "start_dt";

        /**
         * Column name for the activity end date
         * <P>Type: TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")</P>
         */
        public static final String COLUMN_NAME_END_DATE = "end_dt";
        
        /**
         * Column name for the activity task duration
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_DURATION = "duration";       

        /**
         * Column name for the activity repetition
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_REPETITION = "repetition";


        /**
         * Column name for the activity repetition
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_OCCURRENCE = "occurence";
        

        /**
         * Column name for the activity occurence
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_WEEKDAYS = "weekdays";
        

        /**
         * Column name for the activity weekdays
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_NB_TASKS = "nb_tasks";

        /**
         * Column name for the activity progress
         *<P>Type: INTEGER </P>
         */
		public static final String COLUMN_NAME_PROGRESS = "progress";

        /**
         * Column name for the activity background color
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_GOAL_COLOR = "goal_color";

        /**
         * Column name for the tasks' recurrence rule
         *<P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_RRULE = "rrule";

        /**
         * Column name for the activity nb of tasks
         * It is not needed as '_ID' is provided by the parent class BaseColumns 
        */        
    }    

    /**
     * Tasks table contract
     */
    public static final class Tasks implements BaseColumns {

        // This class cannot be instantiated
        private Tasks() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "task";

        /*
         * URI definitions
         */
        
        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */

        /**
         * Path part for the Tasks URI
         */
        private static final String PATH_TASKS = "/tasks";

        /**
         * Path part for the Task ID URI
         */
        private static final String PATH_TASK_ID = "/tasks/";

        /**
         * 0-relative position of a task ID segment in the path part of an task ID URI
         */
        public static final int TASK_ID_PATH_POSITION = 1;
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_TASKS);

        /**
         * The content URI base for a single task. Callers must
         * append a numeric task id to this Uri to retrieve a task
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_ID);

        /**
         * The content URI match pattern for a single task, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_ID + "/#");
   
        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of tasks.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gloria.task";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * task.
         */
         
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gloria.task";
        
        /*
         * Column definitions
         */

        /**
         * Column name for the title of the task
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_TITLE = "title";

        /**
         * Column name of the goal id of this task
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_GOAL_ID = "goal_id";

        /**
         * Title of the goal of this task
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_GOAL_TITLE = "goal_title";        
        
        /**
         * Column name of the activity id
         * TODO to define the reference Type <P>Type: ?</P>
         */
        public static final String COLUMN_NAME_ACTIVITY_ID = "activity_id";        
        
        /**
         * Column name for the task due date
         * <P>Type: TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")</P>
         */
        public static final String COLUMN_NAME_DUE_DATE = "due_dt";

        /**
         * Column name for the task start time
         * <P>Type: TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")</P>
         */
        public static final String COLUMN_NAME_START_DATE = "start_dt";
        
        /**
         * Column name for the task done date
         *<P>Type: TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS") </P>
         */
        public static final String COLUMN_NAME_DONE_DATE = "done_dt";       

        /**
         * Column name for the task done boolean
         *<P>TODO to define the boolean Type Type: ? </P>
         */
        public static final String COLUMN_NAME_DONE = "done";

        /**
         * Column name for the task status
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_STATUS = "status";
        
        /*
         * Column name for the task id
         * It is not needed as '_ID' is provided by the parent class BaseColumns 
        */

        /**
         * Column name for the activity background color
         *<P>Type: INTEGER </P>
         */
        public static final String COLUMN_NAME_GOAL_COLOR = "goal_color";

	    /**
	     * The default sort order for this table
	     */
	    // TODO default order
	    public static final String DEFAULT_SORT_ORDER = Tasks.TABLE_NAME +
	    		"." + Tasks.COLUMN_NAME_START_DATE + " ASC";

    }  
    
    public static final class Repetition implements BaseColumns {

        // This class cannot be instantiated
        private Repetition() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "repetition";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /*
         * Column definitions
         */

        /**
         * Column name for the name of the repetititon
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_NAME = "name";
    
    }
    
    public static final class Category implements BaseColumns {

        // This class cannot be instantiated
        private Category() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "category";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /*
         * Column definitions
         */

        /**
         * Column name for the name of the category
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_NAME = "name";
    
        /**
         * Column name for the color of the category
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_COLOR = "color";
        
    }
    
    public static final class Status implements BaseColumns {

        // This class cannot be instantiated
        private Status() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "status";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /*
         * Column definitions
         */

        /**
         * Column name for the name of the status
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_NAME = "name";
    
    }

    
}
