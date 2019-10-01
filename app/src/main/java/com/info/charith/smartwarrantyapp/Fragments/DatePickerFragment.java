package com.info.charith.smartwarrantyapp.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by charith on 5/29/2017.
 */

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;
    DatePickerDialog dialog;


    public DatePickerFragment() {

    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    private int year, month, day;
    private long minDate;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
        minDate=args.getLong("minDate");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Calendar calender = Calendar.getInstance();
        dialog=new DatePickerDialog(getActivity(), ondateSet, year, month, day);
        dialog.getDatePicker().setMinDate(minDate);
        return dialog;
    }

}
