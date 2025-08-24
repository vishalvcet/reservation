package com.example.libraryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

public class UserActivity extends Activity {
    ListView lvBooks;
    Button btnRefresh, btnLogout;
    TextView tvWelcome;
    DBHelper dbHelper;
    ArrayAdapter<String> adapter;
    ArrayList<Integer> bookIds = new ArrayList<>();
    ArrayList<String> display = new ArrayList<>();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        tvWelcome = findViewById(R.id.tvWelcome);
        lvBooks = findViewById(R.id.lvBooksUser);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnLogout = findViewById(R.id.btnLogoutUser);

        username = getIntent().getStringExtra("username");
        if(username==null) username = "guest";
        tvWelcome.setText("Welcome, " + username);

        dbHelper = new DBHelper(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);
        lvBooks.setAdapter(adapter);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { loadBooks(); }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int bookId = bookIds.get(position);
                confirmReserve(bookId);
            }
        });

        loadBooks();
    }

    private void loadBooks() {
        display.clear();
        bookIds.clear();
        Cursor c = dbHelper.getAllBooks();
        if(c!=null) {
            while(c.moveToNext()) {
                int id = c.getInt(0);
                String title = c.getString(1);
                String author = c.getString(2);
                int copies = c.getInt(3);
                String due = c.getString(4);
                display.add(title + " - " + author + " | copies: " + copies + " | due: " + due + " (tap to reserve)");
                bookIds.add(id);
            }
            c.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void confirmReserve(final int bookId) {
        // First check copies
        Cursor bc = dbHelper.getBookById(bookId);
        if(bc==null || !bc.moveToFirst()) {
            Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show();
            if(bc!=null) bc.close();
            return;
        }
        int copies = bc.getInt(3);
        final String dueDate = bc.getString(4);
        final String title = bc.getString(1);
        bc.close();

        if(copies <= 0) {
            Toast.makeText(this, "No copies available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user already reserved today
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor r = dbHelper.getReservationForUserOnDate(username, today);
        boolean already = (r!=null && r.getCount()>0);
        if(r!=null) r.close();

        if(already) {
            Toast.makeText(this, "You already made a reservation today (one per day)", Toast.LENGTH_LONG).show();
            return;
        }

        // Check due date proximity -> fee
        int fee = 0;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dd = fmt.parse(dueDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            long diff = dd.getTime() - cal.getTimeInMillis();
            long days = diff / (1000*60*60*24);
            if(days <= 3) {
                fee = 10; // urgency
            }
        } catch (Exception ex) {
            // parse error -> no fee
            fee = 0;
        }

        String msg = "Reserve \"" + title + "\"?\nDue: " + dueDate + "\nUrgency fee: ₹" + fee;

        final int finalBookId = bookId; // effectively final
        final int finalFee = fee;

        new AlertDialog.Builder(this)
                .setTitle("Confirm Reservation")
                .setMessage(msg)
                .setPositiveButton("Reserve", (dialog, which) -> doReserve(finalBookId, finalFee))
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void doReserve(int bookId, int fee) {
        // decrease copies
        Cursor bc = dbHelper.getBookById(bookId);
        if(bc==null || !bc.moveToFirst()) {
            if(bc!=null) bc.close();
            Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show();
            return;
        }
        int copies = bc.getInt(3);
        bc.close();
        int newCopies = Math.max(0, copies - 1);
        dbHelper.updateCopies(bookId, newCopies);

        // add reservation entry with today's date
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dbHelper.addReservation(username, bookId, today, fee);

        Toast.makeText(this, "Reserved. Fee: ₹" + fee, Toast.LENGTH_LONG).show();
        loadBooks();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
