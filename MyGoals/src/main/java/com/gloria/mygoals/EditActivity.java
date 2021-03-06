package com.gloria.mygoals;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import be.billington.calendar.recurrencepicker.EventRecurrence;
import be.billington.calendar.recurrencepicker.EventRecurrenceFormatter;
import be.billington.calendar.recurrencepicker.RecurrencePickerDialog;

public class EditActivity extends FragmentActivity implements usesDatePickerDialogInterface, usesTimePickerDialogInterface, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // Frequency spinner choices
    static final int NONE = 0;
    // Intent extra parameters' interface
    static final String EXTRA_KEY_MODE = "mode";
    static final String EXTRA_KEY_GOAL_ID = "goal_id";
    static final String EXTRA_KEY_GOAL_TITLE = "goal_title";
    // For log purpose
    private final static String TAG = "EditActivity";
    private static final long ALARM_DELAY = 15 * 60 * 1000L;
    // activity state
    private Mode mMode = Mode.NEW;
    ;
    private int mGoalId = 0;
    private String mGoalTitle;
    // form views
    private TextView mVGoalTitle;
    private EditText mVActivityTitle;
    private EditText mVActivityDesc;
    private TextView mVTaskDuration;
    private TextView mVStartDate;
    private TextView mVStartTime;
    private TextView mVFrequencyChoice;
    // TODO To add the workload field in the activity table
    private Button mSubmitBtn;
    private Button mCancelBtn;
    // For the RecurrencePickerDialog
    private Bundle bundle;
    private EventRecurrence taskRecurrence;
    private String mRrule;
    // form values
    private Date mStartDate;
    private Date mStartTime;
    private int mDuration = 3600;
    private Date mEndDate;
    private int mNbTasks;
    // for DatePickerDialog purpose
    private Date mCurrentDate;
    private TextView mCurrentDateTextView;
    // for notifications purpose
    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate method");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle(getResources().getString(R.string.my_activity));

        // Set the mode and the Goal id thanks to the intent extra values
        mMode = (Mode) getIntent().getExtras().get(EXTRA_KEY_MODE);
        mGoalId = getIntent().getIntExtra(EXTRA_KEY_GOAL_ID, 0);
        mGoalTitle = getIntent().getStringExtra(EXTRA_KEY_GOAL_TITLE);

        // Set the action bar
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // TODO pb with theme.holo.dialog
//            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create PendingIntent to start the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(this, NotificationReceiver.class);
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(this, 0, mNotificationReceiverIntent, 0);

        // Init the views' values and listeners
        initViews();
    }

    private void initViews() {
        Log.d(TAG, "initViews method");

        // Get the layout's views
        mVGoalTitle = (TextView) findViewById(R.id.l_activity_goalTitle);
        mVActivityTitle = (EditText) findViewById(R.id.t_activity_title);
        mVActivityDesc = (EditText) findViewById(R.id.t_activity_description);
        mVTaskDuration = (TextView) findViewById(R.id.t_task_duration);
        mVStartDate = (TextView) findViewById(R.id.t_activity_start_date);
        mVStartTime = (TextView) findViewById(R.id.t_activity_start_time);
        mVFrequencyChoice = (TextView) findViewById(R.id.t_frequency);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel);

        // Set the Goal Title
        mVGoalTitle.setText(mGoalTitle);

        // Set the submit button label and init the fields values
        switch (mMode) {
            case NEW:    // Set the label "Create" on the submit button
                mSubmitBtn.setText(R.string.create);
                fillWithDefaultValues();
                break;
            case EDIT:    // Set the label "Modify" on the submit button
                mSubmitBtn.setText(R.string.modify);
                // TODO Modification Mode: fillWithActivityValues(mActivityId);
                break;
            default:
        }

        // Set the view listeners
        mSubmitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick method on the submit button");
                // validate the form values
                if (validateForm()) {
                    switch (mMode) {
                        case NEW:
                            insertInDB();
                            break;
                        case EDIT:
                            // TODO Modification Mode: updateInDB(mGoalId);
                            break;
                    }
                    EditActivity.this.finish();        // quit the activity
                }
            }
        });

        mCancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick method on the cancel button");
                finish();
            }
        });

        mVStartDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick method on the Start Date textView");
                // Init the view's references
                mCurrentDateTextView = (TextView) v;
                mCurrentDate = mStartDate;
                // Show the DatePickerDialog
                showDatePickerDialog();
            }
        });


        mVStartTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick method on the Start Time textView");
                Log.v(TAG, "The id of the clicked view is " + v.getId());
                // Init the view's references
                mCurrentDateTextView = (TextView) v;
                mCurrentDate = mStartDate;
                // Show the DatePickerDialog
                showTimePickerDialog();
            }
        });

        mVTaskDuration.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick method on the task duration textView");
                Log.v(TAG, "The id of the clicked view is " + v.getId());

                // Show the DurationPickerDialog
                showDurationPickerDialog();
            }
        });

        mVFrequencyChoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Time startDate = new Time();
                startDate.set(mStartDate.getTime());

                if (null == bundle) bundle = new Bundle();
                bundle.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, startDate.toMillis(false));

                final RecurrencePickerDialog dialog = new RecurrencePickerDialog();
                dialog.setArguments(bundle);

                dialog.setOnRecurrenceSetListener(new RecurrencePickerDialog.OnRecurrenceSetListener() {
                    @Override
                    public void onRecurrenceSet(String rrule) {
                        Log.v(TAG, rrule == null ? "" : rrule);

                        if (null != rrule) {
                            if (null == taskRecurrence) taskRecurrence = new EventRecurrence();
                            taskRecurrence.parse(rrule);
                            taskRecurrence.setStartDate(startDate);

                            bundle.putString(RecurrencePickerDialog.BUNDLE_RRULE, rrule);

                            mVFrequencyChoice.setText(EventRecurrenceFormatter.getRepeatString(getApplicationContext(), getResources(), taskRecurrence, true));
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "recurrencePicker");
            }
        });

    }

    private boolean validateForm() {
        Log.d(TAG, "validateForm method");
        if (mVActivityTitle.getText().toString().isEmpty()
                || mVFrequencyChoice.getText().toString().isEmpty()
                || mVStartDate.getText().toString().isEmpty()
                || mVStartTime.getText().toString().isEmpty()
                || mDuration <= 0
                ) {
            Log.d(TAG, "Error validateForm method: Title Or Description Or WorkLoad is empty");

            Toast.makeText(this, getResources().getText(R.string.forms_empty), Toast.LENGTH_LONG).show();

            return false;

        }

        return true;
        //return false;
    }

    private void insertInDB() {
        Log.d(TAG, "insertInDB method");

        // Array of TaskDates to store the start date&time and end date&time of the tasks
        ArrayList<TaskDates> tasks = computeListOfTasks();

        // TODO Raise an exception if not inserted in DB
        Uri res = insertActivityInDB();
        // TODO Raise an exception if not a parseable string
        int activityId = Integer.parseInt(res.getPathSegments().get(1));
        // Update the goal start and target date if the activity is over the goal dates.
        // TODO to consider the updateGoalDate result
        updateGoalDate();

        for (TaskDates task : tasks) {
            if (null != insertTaskInDB(activityId, task)) {
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, task.startDate.getTime() - ALARM_DELAY, mNotificationReceiverPendingIntent);
                Log.v(TAG, "current time:" + (new Date()).getTime() + ",alarm time:" + (task.startDate.getTime() - ALARM_DELAY));
            }
        }
    }

    private Uri insertActivityInDB() {
        Log.d(TAG, "insertActivityInDB method");

        // Query the DB through a contentProvider, though use a contentResolver
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        values.put(MyGoals.Activities.COLUMN_NAME_GOAL_ID, mGoalId);
        values.put(MyGoals.Activities.COLUMN_NAME_TITLE, mVActivityTitle.getText().toString());
        values.put(MyGoals.Activities.COLUMN_NAME_DESC, mVActivityDesc.getText().toString());

        //Dates are stored as UTC date strings ("YYYY-MM-DD HH:mmZ")
        values.put(MyGoals.Activities.COLUMN_NAME_START_DATE, sdf.format(mStartDate));
        values.put(MyGoals.Activities.COLUMN_NAME_DURATION, mDuration);

        if (null != taskRecurrence) {
            values.put(MyGoals.Activities.COLUMN_NAME_REPETITION, taskRecurrence.freq);
            // The week checkbox values are stored in DB as a String of boolean values eg. "true&false&..."
            values.put(MyGoals.Activities.COLUMN_NAME_NB_TASKS, mNbTasks);
            //values.put(MyGoals.Activities.COLUMN_NAME_WEEKDAYS, taskRecurrence.byday);
            values.put(MyGoals.Activities.COLUMN_NAME_OCCURRENCE, taskRecurrence.interval);
            values.put(MyGoals.Activities.COLUMN_NAME_RRULE, taskRecurrence.toString());

        } else {
            values.put(MyGoals.Activities.COLUMN_NAME_REPETITION, NONE);
            values.put(MyGoals.Activities.COLUMN_NAME_NB_TASKS, 1);
            values.put(MyGoals.Activities.COLUMN_NAME_OCCURRENCE, 1);
        }

        values.put(MyGoals.Activities.COLUMN_NAME_END_DATE, sdf.format(mEndDate));
        values.put(MyGoals.Activities.COLUMN_NAME_PROGRESS, 0);

        Uri res = cr.insert(MyGoals.Activities.CONTENT_URI, values);

        // Update the workload baseline of the goal
        Uri uri = Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + mGoalId);
        Cursor goalCursor = cr.query(uri,
                new String[]{MyGoals.Goals.COLUMN_NAME_WORKLOAD},
                null, null, null);

        goalCursor.moveToFirst();

        int workload = goalCursor.getInt(goalCursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_WORKLOAD));
        workload += mNbTasks * mDuration;

        goalCursor.close();

        values.clear();
        values.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD, workload);
        int nbGoal = cr.update(uri, values, null, null);

        if (nbGoal != 1) {
            Log.v(TAG, "Error when updating the goal workload");
        }

        return res;
    }

    private boolean updateGoalDate() {
        Log.d(TAG, "updateGoalDate method");
        // update the goal's progress
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        ContentResolver cr = getContentResolver();
        Date startDate, targetDate;

        Uri uri = Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + mGoalId);
        Cursor cursor = cr.query(uri, new String[]{MyGoals.Goals.COLUMN_NAME_START_DATE, MyGoals.Goals.COLUMN_NAME_TARGET_DATE}, null, null, null);

        cursor.moveToFirst();
        String sStartDate = cursor.getString(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_START_DATE));
        String sTargetDate = cursor.getString(cursor.getColumnIndex(MyGoals.Goals.COLUMN_NAME_TARGET_DATE));
        cursor.close();

        try {
            startDate = sdf.parse(sStartDate);
            targetDate = sdf.parse(sTargetDate);
        } catch (ParseException e) {
            return false;
        }

        ContentValues values = new ContentValues();
        if (mStartDate.before(startDate)) {
            values.put(MyGoals.Goals.COLUMN_NAME_START_DATE, sdf.format(mStartDate));
        }
        if (mEndDate.after(targetDate)) {
            values.put(MyGoals.Goals.COLUMN_NAME_TARGET_DATE, sdf.format(mEndDate));
        }
        int res = 0;
        if (values.size() > 0) {
            res = cr.update(uri, values, null, null);
        }
        if (res != 1) {
            Log.v(TAG, "Update error");
            return false;
        }
        return true;
    }

    private Uri insertTaskInDB(int activityId, TaskDates task) {
        Log.d(TAG, "insertTaskInDB method");

        // Query the DB through a contentProvider, though use a contentResolver
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        String taskTitle = mVActivityTitle.getText().toString();
        if (mNbTasks > 1) {
            taskTitle += " (" + (task.index + 1) + "/" + mNbTasks + ")";
        }
        ;
        values.put(MyGoals.Tasks.COLUMN_NAME_TITLE, taskTitle);
        values.put(MyGoals.Tasks.COLUMN_NAME_GOAL_ID, mGoalId);
        values.put(MyGoals.Tasks.COLUMN_NAME_ACTIVITY_ID, activityId);
        values.put(MyGoals.Tasks.COLUMN_NAME_DUE_DATE, sdf.format(task.endDate));
        values.put(MyGoals.Tasks.COLUMN_NAME_START_DATE, sdf.format(task.startDate));

        values.put(MyGoals.Tasks.COLUMN_NAME_DONE, Boolean.FALSE.toString());
        // TODO missing default values for MyGoals.Tasks.COLUMN_NAME_DONE_DATE; MyGoals.Tasks.COLUMN_NAME_STATUS;

        return cr.insert(MyGoals.Tasks.CONTENT_URI, values);
    }

    private void fillWithDefaultValues() {
        Log.d(TAG, "fillWithDefaultValues method");

        // init the start date & time with the current date & time
        mStartDate = new Date();
        mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
        mVStartTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mStartDate));

        // init the task duration to 1:00
        mDuration = 3600;
        String dur = DateUtils.formatElapsedTime(mDuration);

        if (mDuration < 3600) {
            mVTaskDuration.setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.minutes));
        } else {
            mVTaskDuration.setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.hours));
        }

        // Compute the task's end date
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTime(mStartDate);
        endDate.add(Calendar.SECOND, mDuration);
        mEndDate = endDate.getTime();
        mNbTasks = 1;
    }

    private ArrayList<TaskDates> computeListOfTasks() {
        // the result object
        ArrayList<TaskDates> listOfTask = new ArrayList<TaskDates>();

        // Convert the task's start date to a calendar object
        Calendar startDate = Calendar.getInstance(Locale.US);
        Calendar endDate = Calendar.getInstance(Locale.US);
        startDate.setTime(mStartDate);

        // Compute the task's end date
        endDate.setTime(mStartDate);
        endDate.add(Calendar.SECOND, mDuration);

        // CASE OF ONE-TASK ACTIVITY (task dates equals activity dates)
        if (null == taskRecurrence) {
            mEndDate = endDate.getTime();
            mNbTasks = 1;
            listOfTask.add(new TaskDates(1, mStartDate, mEndDate));
            return listOfTask;
        }

        // CASE OF MULTITASKS ACTIVITY
        // Get the interval
        int interval = taskRecurrence.interval <= 1 ? 1 : taskRecurrence.interval;

        // Get the end activity occurrences criterion
        //      Tasks are determined in function of the activity's end date
        //      Convert the activity's end date to a calendar object
        GregorianCalendar activityEnd = new GregorianCalendar();
        boolean bFixedEndDate = taskRecurrence.until != null;
        if (bFixedEndDate) {
            try {
                Time t = new Time();
                t.parse(taskRecurrence.until);
                activityEnd.setTime(new Date(t.normalize(false)));
            } catch (TimeFormatException e) {
            }
        }

        // Set the period unity between 2 consecutive tasks
        int unity;
        switch (taskRecurrence.freq) {
            case EventRecurrence.DAILY:
                unity = Calendar.DAY_OF_YEAR;
                break;
            case EventRecurrence.WEEKLY:
                unity = Calendar.WEEK_OF_YEAR;
                break;
            case EventRecurrence.MONTHLY:
                unity = Calendar.MONTH;
                break;
            default:
                unity = Calendar.WEEK_OF_YEAR;
                break;
        }

        // 1 - Add the 1st occurrence
        int i = 0, d = 0;
        if (taskRecurrence.bydayCount > 1) {
            // It is a normal case for weekly recurrence only
            if (taskRecurrence.freq == EventRecurrence.WEEKLY) {
                boolean bFound = false;
                if (null != taskRecurrence.byday) {
                    int nextWeekDay = taskRecurrence.startDate.weekDay;
                    for (int j = 0; j < 7 && !bFound; j++) {
                        nextWeekDay = (taskRecurrence.startDate.weekDay + j) % 7;

                        if (taskRecurrence.startDate.weekDay + j == 7) {
                            // next week
                            startDate.add(unity, interval);
                            endDate.add(unity, interval);
                            Log.d(TAG, "1-Next week");
                        }

                        for (d = 0; d < taskRecurrence.byday.length; d++) {
                            Log.d(TAG, "j:" + j + ",startWeekDay:" + taskRecurrence.startDate.weekDay + ",nextWeekDay:" + nextWeekDay + ",d:" + d + ",selectDay:" + EventRecurrence.day2TimeDay(taskRecurrence.byday[d]));
                            if (EventRecurrence.day2TimeDay(taskRecurrence.byday[d]) == nextWeekDay) {
                                startDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[d]));
                                endDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[d]));
                                bFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        // Add the task to the result list
        if ((bFixedEndDate && (endDate.before(activityEnd) || endDate.equals(activityEnd)))
                || (!bFixedEndDate && i < taskRecurrence.count)) {
            listOfTask.add(new TaskDates(i++, startDate.getTime(), endDate.getTime()));
        } else {
            return null;
        }

        // 2 - Add the next occurrences
        while (true) {
            // Increment the dates for the next task
            if (taskRecurrence.bydayCount == 0) {
                // Normal case for monthly and daily recurrence
                if (taskRecurrence.freq == EventRecurrence.MONTHLY || taskRecurrence.freq == EventRecurrence.DAILY) {
                    startDate.add(unity, interval);
                    endDate.add(unity, interval);
                }
            } else if (taskRecurrence.bydayCount == 1) {
                // In case of monthly recurrence, it means "every weekday of the Nth week"
                // ex: byday="4MO": every 4th monday, "-1TU": every last tuesday
                if (taskRecurrence.freq == EventRecurrence.MONTHLY) {
                    startDate.add(unity, interval);
                    startDate.set(Calendar.WEEK_OF_MONTH, taskRecurrence.bydayNum[0]);
                    startDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[0]));
                    endDate.add(unity, interval);
                    endDate.set(Calendar.WEEK_OF_MONTH, taskRecurrence.bydayNum[0]);
                    endDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[0]));
                }
                // In case of weekly recurrence, it means "every weekday of the week"
                // ex: byday="MO": every monday, "TU": every tuesday
                if (taskRecurrence.freq == EventRecurrence.WEEKLY) {
                    startDate.add(unity, interval);
                    startDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[0]));
                    endDate.add(unity, interval);
                    endDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[0]));
                }
            } else if (taskRecurrence.bydayCount > 1) {
                // Normal case for weekly recurrence only
                Log.d(TAG, "2-d:" + d + ",taskRecurrence.byday.length:" + taskRecurrence.byday.length);
                if (taskRecurrence.freq == EventRecurrence.WEEKLY) {
                    d++;
                    if (d == taskRecurrence.byday.length) {
                        startDate.add(unity, interval);
                        endDate.add(unity, interval);
                        d = 0;
                        Log.d(TAG, "2-Next week");
                    }
                    startDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[d]));
                    endDate.set(Calendar.DAY_OF_WEEK, EventRecurrence.day2CalendarDay(taskRecurrence.byday[d]));
                    Log.d(TAG, "startWeekDay:" + taskRecurrence.startDate.weekDay + ",d:" + d + ",selectDay:" + EventRecurrence.day2TimeDay(taskRecurrence.byday[d]));
                }
            }

            if ((bFixedEndDate && (endDate.before(activityEnd) || endDate.equals(activityEnd)))
                    || (!bFixedEndDate && i < taskRecurrence.count)) {
                // Add the task to the result list
                listOfTask.add(new TaskDates(i++, startDate.getTime(), endDate.getTime()));
            } else {
                break;
            }
        }

        // Update the Activity's end date & Nb of tasks
        mNbTasks = listOfTask.size();
        try {
            mEndDate = listOfTask.get(mNbTasks - 1).endDate;
        } catch (Exception e) {
            mEndDate = endDate.getTime();
        }

        return listOfTask;
    }

    public void showDatePickerDialog() {
        Log.d(TAG, "showDatePickerDialog method");

        final Calendar c = Calendar.getInstance();
        // Use the current date as the default date in the picker
        if (mCurrentDate != null) {
            c.setTime(mCurrentDate);
        }

        DatePickerDialog dialog = DatePickerDialog.newInstance(this, c.get(c.YEAR), c.get(c.MONTH), c.get(c.DAY_OF_MONTH), false);

        /*dialog.setVibrate(isVibrate());
        dialog.setYearRange(1985, 2028);*/

        dialog.show(this.getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        DatePickerCallBack(year, month, day);
    }

    public void showTimePickerDialog() {
        Log.d(TAG, "showTimePickerDialog method");

        final Calendar c = Calendar.getInstance();
        // Use the current date as the default date in the picker
        if (mCurrentDate != null) {
            c.setTime(mCurrentDate);
        }

        TimePickerDialog dialog = TimePickerDialog.newInstance(this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true, false);
        dialog.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDurationPickerDialog() {
        Log.d(TAG, "showDurationPickerDialog method");

        android.app.TimePickerDialog dialog = new android.app.TimePickerDialog(this,
                new android.app.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mDuration = hourOfDay * 3600 + minute * 60;

                        String dur = DateUtils.formatElapsedTime(mDuration);
                        if (mDuration < 3600) {
                            mVTaskDuration.setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.minutes));
                        } else {
                            mVTaskDuration.setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.hours));
                        }

                        // Recompute the View parameters
                        computeListOfTasks();
                    }
                }, mDuration / 3600, mDuration / 60, true);

        dialog.show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        TimePickerCallBack(hourOfDay, minute);
    }

    @Override
    // Used to init the default date or time in the date or time pickers
    public Date getCurrentDate() {
        return mCurrentDate;
    }

    @Override
    // called when a date is set in a date picker
    public void DatePickerCallBack(int year, int month, int day) {
        if (mCurrentDateTextView == mVStartDate) {
            // Format the date to the user's locales and set the destination view
            final Calendar c = Calendar.getInstance();
            c.setTime(mStartDate);
            c.set(year, month, day);
            mStartDate.setTime(c.getTimeInMillis());
            mVStartDate.setText(SimpleDateFormat.getDateInstance().format(mStartDate));
            // Recompute the View parameters
            computeListOfTasks();
            // Update the Activity's end date & nb of tasks
            /*mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
			mVNbTasks.setText("" + mNbTasks);*/
        } else {
            Log.w(TAG, "Setting a date in a DatePicker dialog should update a View");
        }

    }

    @Override
    // called when a time is set in a time picker
    public void TimePickerCallBack(int hourOfDay, int minute) {
        if (mCurrentDateTextView == mVStartTime) {
            // Format the date to the user's locales and set the destination view
            final Calendar c = Calendar.getInstance();
            c.setTime(mStartDate);
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            mStartDate.setTime(c.getTimeInMillis());
            mVStartTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mStartDate));
            // Recompute the View parameters
            computeListOfTasks();
            // Update the Activity's end date & nb of tasks
			/*mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
			mVNbTasks.setText("" + mNbTasks);*/
        }
        //TODO to remove, useless code
        else if (mCurrentDateTextView == mVTaskDuration) {
            // Format the date to the user's locales and set the destination view
            mDuration = hourOfDay * 3600 + minute * 60;

            //mVTaskDuration.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(mDuration));
            String dur = DateUtils.formatElapsedTime(mDuration);
            if (mDuration < 3600) {
                mVTaskDuration.setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.minutes));
            } else {
                mVTaskDuration.setText(dur.substring(0, dur.length() - 3) + " " + getString(R.string.hours));
            }

            // Recompute the View parameters
            computeListOfTasks();
            // Update the Activity's end date & nb of tasks
			/*mVEndDate.setText(SimpleDateFormat.getDateTimeInstance().format(mEndDate));
			mVNbTasks.setText("" + mNbTasks); */
        } else {
            Log.w(TAG, "Setting a time in a TimePicker dialog should update a View");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu method");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected method");
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                // TODO proper Up Navigation : NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static enum Mode {NEW, EDIT}

    // Inner class TaskDates stores the start date&time and the end date&time of a task
    private class TaskDates {
        // 0-based index
        public int index;
        public Date startDate;
        public Date endDate;

        public TaskDates(int index, Date startDate, Date endDate) {
            this.index = index;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
