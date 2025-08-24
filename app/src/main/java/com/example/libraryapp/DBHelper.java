package com.example.libraryapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {
    private SQLiteDatabase db;

    public DBHelper(Context ctx) {
        db = ctx.openOrCreateDatabase("LibraryDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, author VARCHAR, copies INTEGER, due_date VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS reservations(id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, book_id INTEGER, reserved_date VARCHAR, fee INTEGER);");

        // Insert inbuilt books if table empty
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM books", null);
        if (c.moveToFirst() && c.getInt(0) == 0) {
            insertDefaultBooks();
        }
        c.close();
    }

    private void insertDefaultBooks() {
        addBook("Ponniyin Selvan", "Kalki Krishnamurthy", 5, "2025-08-15");
        addBook("The Alchemist", "Paulo Coelho", 4, "2025-08-10");
        addBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 6, "2025-08-20");
        addBook("Wings of Fire", "A.P.J. Abdul Kalam", 3, "2025-08-09");
        addBook("The Great Gatsby", "F. Scott Fitzgerald", 5, "2025-08-12");
        addBook("The Power of Your Subconscious Mind", "Joseph Murphy", 4, "2025-08-14");
        addBook("Rich Dad Poor Dad", "Robert Kiyosaki", 3, "2025-08-11");
        addBook("Think and Grow Rich", "Napoleon Hill", 5, "2025-08-18");
        addBook("The Secret", "Rhonda Byrne", 4, "2025-08-19");
        addBook("The Monk Who Sold His Ferrari", "Robin Sharma", 6, "2025-08-16");
    }

    public void addBook(String title, String author, int copies, String dueDate) {
        db.execSQL("INSERT INTO books(title,author,copies,due_date) VALUES('" + escape(title) + "','" + escape(author) + "'," + copies + ",'" + dueDate + "');");
    }

    public Cursor getAllBooks() {
        return db.rawQuery("SELECT id,title,author,copies,due_date FROM books ORDER BY id DESC", null);
    }

    public Cursor getBookById(int id) {
        return db.rawQuery("SELECT id,title,author,copies,due_date FROM books WHERE id=" + id, null);
    }

    public void updateCopies(int bookId, int newCopies) {
        db.execSQL("UPDATE books SET copies=" + newCopies + " WHERE id=" + bookId);
    }

    public Cursor getReservationForUserOnDate(String username, String date) {
        return db.rawQuery("SELECT id FROM reservations WHERE username='" + escape(username) + "' AND reserved_date='" + date + "'", null);
    }

    public void addReservation(String username, int bookId, String reservedDate, int fee) {
        db.execSQL("INSERT INTO reservations(username,book_id,reserved_date,fee) VALUES('" + escape(username) + "'," + bookId + ",'" + reservedDate + "'," + fee + ");");
    }

    public Cursor getReservationsForUser(String username) {
        return db.rawQuery("SELECT r.id,b.title,r.reserved_date,r.fee FROM reservations r JOIN books b ON r.book_id=b.id WHERE r.username='" + escape(username) + "' ORDER BY r.id DESC", null);
    }

    public void close() {
        db.close();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "''");
    }
}
