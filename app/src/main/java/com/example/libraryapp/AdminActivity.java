package com.example.libraryapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {  // <-- Changed to AppCompatActivity
    Button btnAddBook, btnViewBooks, btnLogout;
    ListView lvBooks;
    DBHelper dbHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Enable ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Admin Panel");
        }

        btnAddBook = findViewById(R.id.btnAddBook);
        btnViewBooks = findViewById(R.id.btnViewBooksAdmin);
        btnLogout = findViewById(R.id.btnLogoutAdmin);
        lvBooks = findViewById(R.id.lvBooksAdmin);

        dbHelper = new DBHelper(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvBooks.setAdapter(adapter);

        btnAddBook.setOnClickListener(v ->
                startActivity(new android.content.Intent(AdminActivity.this, AddBookActivity.class))
        );

        btnViewBooks.setOnClickListener(v -> loadBooks());

        btnLogout.setOnClickListener(v -> finish());

        loadBooks();
    }

    private void loadBooks() {
        items.clear();
        Cursor c = dbHelper.getAllBooks();
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String title = c.getString(1);
                String author = c.getString(2);
                int copies = c.getInt(3);
                String due = c.getString(4);
                items.add("ID:" + id + " | " + title + " - " + author + " | copies:" + copies + " | due:" + due);
            }
            c.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    // Handle ActionBar back button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
