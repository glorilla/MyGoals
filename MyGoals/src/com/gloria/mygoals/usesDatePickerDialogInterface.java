package com.gloria.mygoals;

import java.util.Date;
import android.widget.TextView;

public interface usesDatePickerDialogInterface {

	public Date getCurrentDate();

	//public TextView getCurrentDateTextView();

	public void DatePickerCallBack(int year, int month, int day);

}
