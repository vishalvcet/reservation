package com.example.libraryapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.Calendar;

public class AddBookActivity extends Activity {
    EditText etTitle, etAuthor, etCopies;
    Button btnPickDate, btnSave;
    TextView tvDueDate;
    DBHelper dbHelper;
    String selectedDate = ""; // yyyy-MM-dd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etCopies = findViewById(R.id.etCopies);
        btnPickDate = findViewById(R.id.btnPickDate);
        tvDueDate = findViewById(R.id.tvDueDate);
        btnSave = findViewById(R.id.btnSaveBook);
        dbHelper = new DBHelper(this);

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { pickDate(); }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { saveBook(); }
        });
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int mm = month + 1;
                selectedDate = String.format("%04d-%02d-%02d", year, mm, dayOfMonth);
                tvDueDate.setText("Due Date: " + selectedDate);
            }
        }, y, m, d);
        dp.show();
    }

    private void saveBook() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String copiesS = etCopies.getText().toString().trim();
        if(title.length()==0 || author.length()==0 || copiesS.length()==0 || selectedDate.length()==0) {
            Toast.makeText(this, "Enter all fields and pick due date", Toast.LENGTH_SHORT).show();
            return;
        }
        int copies = Integer.parseInt(copiesS);
        dbHelper.addBook(title, author, copies, selectedDate);
        Toast.makeText(this, "Book saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
