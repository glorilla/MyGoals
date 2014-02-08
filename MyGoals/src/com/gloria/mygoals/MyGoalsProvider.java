/**
 * 
 */
package com.gloria.mygoals;

import java.util.HashMap;

import com.gloria.mygoals.MyGoals;

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
    private static final int DATABASE_VERSION = 3;

    /**
     * Projection maps used to select columns from the database
     */
    private static HashMap<String, String> sGoalsProjectionMap;
    
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
            MyGoals.Goals.COLUMN_NAME_PROGRESS	// Projection position 6, the goal's progress
    };
    
    /*
     * Indexes of the columns in the projection 
     */
    public static final int GOAL_TITLE_INDEX = 1;
    public static final int GOAL_DESC_INDEX = 2;
    public static final int GOAL_START_DATE_INDEX = 3;
    public static final int GOAL_TARGET_DATE_INDEX = 4;
    public static final int GOAL_WORKLOAD_INDEX = 5;
    public static final int GOAL_PROGRESS_INDEX = 6;
         
    /*
     * Constants used by the Uri matcher to choose an action based on the pattern
     * of the incoming URI
     */
    // The incoming URI matches the Goals URI pattern
    private static final int GOALS = 1;

    // The incoming URI matches the Goal ID URI pattern
    private static final int GOAL_ID = 2;

    // The incoming URI matches the Live Folder URI pattern
    // TODO to remove or modify
    //private static final int LIVE_FOLDER_NOTES = 3;

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

        // Add a pattern that routes URIs terminated with "goals" to a NOTES operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "goals", GOALS);

        // Add a pattern that routes URIs terminated with "goals" plus an integer
        // to a note ID operation
        sUriMatcher.addURI(MyGoals.AUTHORITY, "goals/#", GOAL_ID);

        // Add a pattern that routes URIs terminated with live_folders/notes to a
        // live folder operation
        // TODO to remove or modify
        //sUriMatcher.addURI(NotePad.AUTHORITY, "live_folders/notes", LIVE_FOLDER_NOTES);

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
        
        /*
         * Creates an initializes a projection map for handling Live Folders
         * TODO to remove or modify

        // Creates a new projection map instance
        sLiveFolderProjectionMap = new HashMap<String, String>();

        // Maps "_ID" to "_ID AS _ID" for a live folder
        sLiveFolderProjectionMap.put(LiveFolders._ID, NotePad.Notes._ID + " AS " + LiveFolders._ID);

        // Maps "NAME" to "title AS NAME"
        sLiveFolderProjectionMap.put(LiveFolders.NAME, NotePad.Notes.COLUMN_NAME_TITLE + " AS " +
            LiveFolders.NAME);

         */
    
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
           db.execSQL("CREATE TABLE " + MyGoals.Goals.TABLE_NAME + " ("
                   + MyGoals.Goals._ID + " INTEGER PRIMARY KEY,"
                   + MyGoals.Goals.COLUMN_NAME_TITLE + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_DESC + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_START_DATE + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_TARGET_DATE + " TEXT,"
                   + MyGoals.Goals.COLUMN_NAME_WORKLOAD + " INTEGER,"
                   + MyGoals.Goals.COLUMN_NAME_PROGRESS + " INTEGER"
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
	public Cursor query(Uri uri, String[] projection, String selection,
		String[] selectionArgs, String sortOrder) {
	       // Constructs a new query builder and sets its table name
	       SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	       qb.setTables( MyGoals.Goals.TABLE_NAME);

	       /**
	        * Choose the projection and adjust the "where" clause based on URI pattern-matching.
	        */
	       switch (sUriMatcher.match(uri)) {
	           // If the incoming URI is for notes, chooses the Notes projection
	           case GOALS:
	               qb.setProjectionMap(sGoalsProjectionMap); // <"a","b as a"> used for 'select "b as a"'
	               break;

	           /* If the incoming URI is for a single note identified by its ID, chooses the
	            * note ID projection, and appends "_ID = <goalID>" to the where clause, so that
	            * it selects that single note
	            */
	           case GOAL_ID:
	               qb.setProjectionMap(sGoalsProjectionMap);
	               qb.appendWhere(
	                   MyGoals.Goals._ID +    // the name of the ID column
	                   "=" +
	                   // the position of the note ID itself in the incoming URI
	                   uri.getPathSegments().get(MyGoals.Goals.GOAL_ID_PATH_POSITION));
	               break;
	           
	           /*
	            * TODO to remove or adapt
	           case LIVE_FOLDER_NOTES:
	               // If the incoming URI is from a live folder, chooses the live folder projection.
	               qb.setProjectionMap(sLiveFolderProjectionMap);
	               break;
			   */
	               
	           default:
	               // If the URI doesn't match any of the known patterns, throw an exception.
	               throw new IllegalArgumentException("Unknown URI " + uri);
	       }


	       String orderBy;
	       // If no sort order is specified, uses the default
	       if (TextUtils.isEmpty(sortOrder)) {
	           orderBy = MyGoals.Goals.DEFAULT_SORT_ORDER;
	       } else {
	           // otherwise, uses the incoming sort order
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
	    	// TODO how Query method deals with provided parameters and QueryBuider config (setProjection, appendWhere, ...)
	           db,            // The database to query
	           projection,    // The columns to return from the query
	           selection,     // The columns for the where clause
	           selectionArgs, // The values for the where clause
	           null,          // don't group the rows
	           null,          // don't filter by row groups
	           orderBy        // The sort order
	       );

	       // Tells the Cursor what URI to watch, so it knows when its source data changes
	       // TODO What's the purpose of setNotificationUri call?
	       // c.setNotificationUri(getContext().getContentResolver(), uri);
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

	           // If the pattern is for notes or live folders, returns the general content type.
	           case GOALS:
	               return MyGoals.Goals.CONTENT_TYPE;

	           // If the pattern is for note IDs, returns the note ID content type.
	           case GOAL_ID:
	               return MyGoals.Goals.CONTENT_ITEM_TYPE;

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
        if (sUriMatcher.match(uri) != GOALS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // A map to hold the new record's values.
        ContentValues values;

        // If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);

        } else {
            // Otherwise
            throw new IllegalArgumentException("Values are missing to insert with URI " + uri);
        }

        // If the values map doesn't contain the creation date, sets the value to the current time.
        if ( values.containsKey(MyGoals.Goals.COLUMN_NAME_TITLE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_START_DATE) == false || 
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_TARGET_DATE) == false ||   
        		values.containsKey(MyGoals.Goals.COLUMN_NAME_WORKLOAD) == false 
        		)  
        {
            // If the mandatory informations are missing, throws an exception.
            throw new IllegalArgumentException("Some mandatory values are missing to insert with URI " + uri);
        }

        // If the values map doesn't contain the modification date, sets the value to the current
        // time.
        if (values.containsKey(MyGoals.Goals.COLUMN_NAME_DESC) == false) {
            values.put(MyGoals.Goals.COLUMN_NAME_DESC, "");
        }

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(
        	MyGoals.Goals.TABLE_NAME,        // The table to insert into.
        	null,  							 // A hack, SQLite sets this column value to null
                                             // if values is empty.
            values                           // A map of column names, and the values to insert
                                             // into the columns.
        );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri goalUri = ContentUris.withAppendedId(MyGoals.Goals.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            // TODO Is it useful?
            // getContext().getContentResolver().notifyChange(noteUri, null);
            return goalUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
