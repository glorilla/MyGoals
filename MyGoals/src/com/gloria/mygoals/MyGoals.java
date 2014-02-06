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
         * Path part for the Live Folder URI
         TODO live_folder: to delete or adapt
        private static final String PATH_LIVE_FOLDER = "/live_folders/notes";
		*/
        
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

        /**
         * The content Uri pattern for a notes listing for live folders
         TODO live_folder: to delete or adapt
        public static final Uri LIVE_FOLDER_URI
            = Uri.parse(SCHEME + AUTHORITY + PATH_LIVE_FOLDER);
		*/
        
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
         * Column name for the goal category
         * TODO category column type ? 

        public static final String COLUMN_NAME_CATEGORY = "cat";     
        */ 
        
        /**
         * Column name for the goal id
         * It is not needed as '_ID' is provided by the parent class BaseColumns 
        */        
    }
}
