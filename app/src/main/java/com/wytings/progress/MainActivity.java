package com.wytings.progress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.wytings.progress.widget.MarkSeekBar;
import com.wytings.progress.widget.TimeProgressView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView currentTimeValue = findViewById(R.id.current_time_value);
        final TimeProgressView progressView = findViewById(R.id.time_progress);
        progressView.setDataList(getTime(0), getTimeData());
        findViewById(R.id.change_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePicker datePicker = new DatePicker(getActivity());
                new AlertDialog.Builder(getActivity()).setView(datePicker)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final int year = datePicker.getYear();
                                final int month = datePicker.getMonth() + 1;
                                final int day = datePicker.getDayOfMonth();
                                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
                                currentTimeValue.setText(date);
                                progressView.setCurrentTime(date);
                            }
                        }).show();
            }
        });

        final TextView progressValue = findViewById(R.id.progress_value);
        final MarkSeekBar markSeekBar = findViewById(R.id.seekBar);
        markSeekBar.setMaxNumber(10);
        markSeekBar.setCurrentNumber(5);
        markSeekBar.setNumberChangeListener(new MarkSeekBar.OnNumberChangeListener() {
            @Override
            public void onChange(float number) {
                progressValue.setText(String.valueOf(number));
            }
        });
        findViewById(R.id.change_seek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"7.2", "8.5", "10"};
                new AlertDialog.Builder(getActivity())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                float value = Float.parseFloat(items[which]);
                                markSeekBar.setMaxNumber(value);
                            }
                        })
                        .show();
            }
        });

    }


    private Activity getActivity() {
        return this;
    }

    public List<TimeProgressView.TimeData> getTimeData() {
        List<TimeProgressView.TimeData> list = new LinkedList<>();
        list.add(new TimeProgressView.TimeData(getTime(-1), "WakeWake"));
        list.add(new TimeProgressView.TimeData(getTime(0), "BedBed"));
        list.add(new TimeProgressView.TimeData(getTime(1), "WashWash"));
        list.add(new TimeProgressView.TimeData(getTime(2), "OutOut"));
        return list;
    }

    private String getTime(int diff) {
        return simpleDateFormat.format(new Date(System.currentTimeMillis() + diff * 30 * 24 * 60 * 60 * 1000L));
    }
}
