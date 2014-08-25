package com.gloria.mygoals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Guillaume on 23/08/2014.
 */
public class EditGoalDialog extends DialogFragment {
    // For log purpose
    private static final String TAG = "EditGoalDialog";
    // Dialog
    AlertDialog mDialog;
    ;
    // form views
    View mLayout;
    EditText mVGoalTitle;
    EditText mVGoalDesc;
    ImageView mVColorPicker;
    // color values
    int mColorId = 0;
    int[] colors = new int[]{0xffffefd5, 0xfffdfd96, 0xfff5f5dc, 0xfff2f3f4, 0xffe9ffdb, 0xffe0ffff};
    // Dialog title
    private String mTitle;
    private int mTitleId = 0;
    // activity state
    private Mode mMode = Mode.NEW;
    private int mGoalId = 0;
    // form values
    private int mColor;
    private int mOKbutton;

    // Constructors
    public EditGoalDialog(String title, Mode mode, int goal_id) {
        mTitle = title;
        // Set the mode and the Goal id
        mMode = mode;
        mGoalId = goal_id;
    }

    public EditGoalDialog(int title, Mode mode, int goal_id) {
        mTitleId = title;
        // Set the mode and the Goal id
        mMode = mode;
        mGoalId = goal_id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog method");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mLayout = inflater.inflate(R.layout.goal_edit_dlg, null);
        // Init the views' values and listeners
        initViews();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        if (mTitle != null) {
            builder.setTitle(mTitle);
        } else {
            builder.setTitle(mTitleId);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mLayout)
                .setPositiveButton(mOKbutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (validateForm()) {
                            switch (mMode) {
                                case NEW:
                                    insertInDB();
                                    break;
                                case EDIT:
                                    updateInDB(mGoalId);
                                    break;
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        mDialog = builder.create();
        return mDialog;
    }

    private void initViews() {
        Log.d(TAG, "initViews method");

        // Get the layout's views
        mVGoalTitle = (EditText) mLayout.findViewById(R.id.t_goal_detail_title);
        mVGoalDesc = (EditText) mLayout.findViewById(R.id.t_goal_detail_description);
        mVColorPicker = (ImageView) mLayout.findViewById(R.id.i_color);

        // Set the submit button label and init the fields values
        switch (mMode) {
            case NEW:    // Set the label "Create" on the submit button
                mOKbutton = R.string.create;
                fillWithDefaultValues();
                break;
            case EDIT:    // Set the label "Modify" on the submit button
                mOKbutton = R.string.modify;
                fillWithGoalValues(mGoalId);
                break;
            default:
        }

        mVColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mVColorPicker.onClick");
                mColorId++;
                mColorId = mColorId % colors.length;
                mColor = colors[mColorId];
                //((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(mColor); //.setBackgroundDrawable(new ColorDrawable(mColor));
                ((AlertDialog) getDialog()).getWindow().findViewById(R.id.root_layout).setBackgroundColor(mColor); //
                // setColorFilter(mColor, PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    private boolean validateForm() {
        Log.d(TAG, "validateForm method");
        if (mVGoalTitle.getText().toString().isEmpty()) {
            Log.d(TAG, "Error validateForm method: Title is empty");
            Toast.makeText(getActivity(), getString(R.string.forms_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        Log.d(TAG, "validateForm method: Ok!");
        return true;
    }

    private void insertInDB() {
        Log.d(TAG, "insertInDB method");

        // Query the DB through a contentProvider, though use a contentResolver
        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        values.put(MyGoals.Goals.COLUMN_NAME_TITLE, mVGoalTitle.getText().toString());
        values.put(MyGoals.Goals.COLUMN_NAME_DESC, mVGoalDesc.getText().toString());

        //Dates are stored as UTC date strings ("YYYY-MM-DD HH:mmZ")
        values.put(MyGoals.Goals.COLUMN_NAME_START_DATE, sdf.format(new Date()));
        values.put(MyGoals.Goals.COLUMN_NAME_TARGET_DATE, sdf.format(new Date()));

        values.put(MyGoals.Goals.COLUMN_NAME_WORKLOAD, 0);
        values.put(MyGoals.Goals.COLUMN_NAME_PROGRESS, 0);
        values.put(MyGoals.Goals.COLUMN_NAME_COLOR, mColor);

        cr.insert(MyGoals.Goals.CONTENT_URI, values);
    }

    private void updateInDB(int id) {
        Log.d(TAG, "fillWithGoalValues method");

        // Query the DB through a contentProvider, though use a contentResolver
        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(MyGoals.Goals.COLUMN_NAME_TITLE, mVGoalTitle.getText().toString());
        values.put(MyGoals.Goals.COLUMN_NAME_DESC, mVGoalDesc.getText().toString());
        values.put(MyGoals.Goals.COLUMN_NAME_COLOR, mColor);

        cr.update(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + id), values, null, null);
    }

    private void fillWithDefaultValues() {
        Log.d(TAG, "fillWithDefaultValues method");
        mColor = colors[mColorId];
        mLayout.setBackgroundColor(mColor);
    }

    private void fillWithGoalValues(int id) {
        Log.d(TAG, "fillWithGoalValues method");
        ContentResolver cr = getActivity().getContentResolver();
        Cursor c = cr.query(Uri.parse(MyGoals.Goals.CONTENT_ID_URI_BASE + "" + id), MyGoalsProvider.GOAL_PROJECTION, null, null, null);
        c.moveToFirst();
        if (c != null) {
            mVGoalTitle.setText(c.getString(MyGoalsProvider.GOAL_TITLE_INDEX));
            mVGoalDesc.setText(c.getString(MyGoalsProvider.GOAL_DESC_INDEX));
            mColor = c.getInt(MyGoalsProvider.GOAL_COLOR_INDEX);
        }
        mLayout.setBackgroundColor(mColor);
    }

    // Dialog mode definition
    public static enum Mode {
        NEW, EDIT
    }
}