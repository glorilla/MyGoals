package com.gloria.mygoals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

	// For log purpose
	private static final  String TAG = "TimePickerFragment"; 	
	
	private Date mDate;
	private TextView mTextView;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG,"onCreateDialog method");
		
		mDate = ((usesDatePickerDialogInterface) getActivity()).getCurrentDate();
		mTextView = ((usesDatePickerDialogInterface) getActivity()).getCurrentDateTextView();

		final Calendar c = Calendar.getInstance();
		// Use the current date as the default date in the picker
		if (mDate != null) {
			c.setTime(mDate);
		} 
	
		// Create a new instance of DatePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()) );
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Log.d(TAG,"onTimeSet method");
		if (mTextView != null) {
			// Format the date to the user's locales and set the destination view
			final Calendar c = Calendar.getInstance();
			c.set(1, 0, 1, hourOfDay, minute);
			mDate.setTime(c.getTimeInMillis());
			mTextView.setText(SimpleDateFormat.getTimeInstance().format(mDate));
			return;
		}
		Log.w(TAG,"Setting a date in this TimePicker dialog should update a View parameter");

		
	}
}

