package com.example.doitchocolate.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.doitchocolate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ReportActivity extends AppCompatActivity {

    private EditText edtFrom, edtTo;
    private CardView btnSubmit;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        edtFrom = (EditText) findViewById(R.id.edt_from);
        edtTo = (EditText) findViewById(R.id.edt_to);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        edtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateFromDialog();
            }
        });

        edtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateToDialog();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String from = edtFrom.getText().toString() + " 00:00:00";
//                String to = edtTo.getText().toString() + " 23:59:59";
                String from = edtFrom.getText().toString();
                String to = edtTo.getText().toString();

                Log.d("[DEBUG]", from + " // " + to);

                Intent i = new Intent(ReportActivity.this, ManageReportListActivity.class);
                i.putExtra("from", from);
                i.putExtra("to", to);
                startActivity(i);
            }
        });
    }

    private void showDateFromDialog() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtFrom.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showDateToDialog() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtTo.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
