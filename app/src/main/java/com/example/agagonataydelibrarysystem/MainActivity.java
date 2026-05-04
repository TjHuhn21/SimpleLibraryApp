package com.example.agagonataydelibrarysystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText bookAuthor,bookISBN,bookTitle, bookID;

    ListView bookList;
    Button addBook, viewBook, updateBook, deleteBook, clearBook;

    SQLiteDatabase db;
    Cursor cursor;
    AlertDialog.Builder builder;
    StringBuffer buffer;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        addBook();
        viewBooks();
        deleteBookRecord();
        updateBookRecord();
    }
    public void init(){

        bookTitle = findViewById(R.id.etTitle);
        bookAuthor = findViewById(R.id.etAuthor);
        bookISBN = findViewById(R.id.etISBN);
        bookID = findViewById(R.id.etBookID);
        bookList = findViewById(R.id.lvBookList);
        addBook = findViewById(R.id.btnAdd);
        viewBook = findViewById(R.id.btnView);
        updateBook = findViewById(R.id.btnUpdate);
        deleteBook = findViewById(R.id.btnDelete);
        clearBook = findViewById(R.id.btnClear);

        builder = new AlertDialog.Builder(this);

        db = openOrCreateDatabase("LibraryDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_books(book_id INTEGER PRIMARY KEY AUTOINCREMENT, book_title TEXT, book_author TEXT, book_isbn TEXT);");
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        bookID.setEnabled(false);
        bookList.setAdapter(adapter);
    }

    public boolean validateInputs(String title, String author, String isbn){
        if(title.isEmpty() || author.isEmpty() || isbn.isEmpty()){
            displayMessage("Error!", "Please fill out all fields");
            return false;
        }
        if (isTitleAllowed(title)) {
            displayMessage("Error", "Special characters like?, :, \", <, >, \\, /, |, and * are not allowed.");
            return false;
        }
        String isbnPattern = "^(\\d{10}|\\d{13})$";
        if (title.length() < 2) {
            displayMessage("Validation Error", "Title is too short.");
            return false;
        }

        if (author.length() > 50) {
            displayMessage("Validation Error", "Author name is too long (max 50 chars).");
            return false;
        }
        if (!isbn.matches(isbnPattern)) {
            displayMessage("Validation Error", "ISBN must be 10 or 13 digits.");
            return false;
        }

        return true;
    }
    public void displayMessage(String title, String message){
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void refreshList() {
        cursor = db.rawQuery("SELECT * FROM tbl_books", null);
        arrayList.clear();
        while (cursor.moveToNext()) {
            // Index 0: ID, Index 1: Title, Index 2: Author
            arrayList.add("ID: " + cursor.getString(0) + "\nTitle: " + cursor.getString(1) + "\nAuthor: " + cursor.getString(2));
        }
        adapter.notifyDataSetChanged(); // Updates the UI
    }
    public boolean isTitleAllowed(String title) {
        String illegalCharacters = "[?:\"<>\\\\/|*]";

        Pattern pattern = java.util.regex.Pattern.compile(illegalCharacters);
        Matcher matcher = pattern.matcher(title);

        if (matcher.find()) {
            displayMessage("Validation Error", "Title contains illegal characters: ?:\"<>\\/|*");
            return false;
        }
        return true;
    }
    public void addBook(){
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = bookTitle.getText().toString().trim();
                String author = bookAuthor.getText().toString().trim();
                String isbn = bookISBN.getText().toString().trim();
                if(!validateInputs(title, author , isbn)){
                    return;
                }
                db.execSQL("INSERT INTO tbl_books (book_title, book_author, book_isbn) VALUES ('" + title + "', '" + author + "', '" + isbn + "');");
                displayMessage("Success", "Book added successfully!");
                refreshList();
                clearInputs();

            }
        });
    }
    public void clearInputs(){
        bookTitle.setText("");
        bookAuthor.setText("");
        bookISBN.setText("");
        bookTitle.requestFocus();
    }

    public void viewBooks(){
        viewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor = db.rawQuery("SELECT * FROM tbl_books", null);

                if (cursor.getCount() == 0) {
                    displayMessage("Information", "No Book Records Found!");
                    return;
                }

                arrayList.clear();

                while (cursor.moveToNext()) {
                    String record = "ID: " + cursor.getString(0) +
                            "\nTitle: " + cursor.getString(1) +
                            "\nAuthor: " + cursor.getString(2) +
                            "\nISBN: " + cursor.getString(3);

                    arrayList.add(record);
                }

                adapter.notifyDataSetChanged();

                displayMessage("Success", "List Updated!");
            }
        });
    }
    public void deleteBookRecord(){
        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookID.getText().toString().isEmpty()){
                    bookID.setEnabled(true);
                    displayMessage("Error", "Input bookID First");
                    return;
                }
                String id = bookID.getText().toString();
                cursor = db.rawQuery("SELECT * FROM tbl_books WHERE book_id = " + id, null);
                if (cursor.moveToFirst()) {
                    db.execSQL("DELETE FROM tbl_books WHERE book_id = " + id);
                    displayMessage("Information", "Book Deleted!");
                    refreshList();
                    clearInputs();
                } else {
                    displayMessage("Error", "Book ID not found");
                }
            }
        });
    }
    public void updateBookRecord(){
        updateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookID.getText().toString().isEmpty()){
                    bookID.setEnabled(true);
                    displayMessage("Information", "Please Enter a Book ID to update");
                    updateBook.setText("SAVE");
                    bookID.requestFocus();
                    return;
                }
                String id = bookID.getText().toString().trim();
                String title = bookTitle.getText().toString().trim();
                String author = bookAuthor.getText().toString().trim();
                String isbn = bookISBN.getText().toString().trim();

                if (id.isEmpty()){
                    bookID.setEnabled(true);
                    displayMessage("Information", "Please Enter a Book ID to update");
                    updateBook.setText("SAVE");
                    bookID.requestFocus();
                    return;
                }

                // 2. Check if the ID exists in the database
                cursor = db.rawQuery("SELECT * FROM tbl_books WHERE book_id =" + Integer.parseInt(id), null);

                if (cursor.moveToFirst()){
                    if (!isTitleAllowed(title)) {
                        return;
                    }

                    db.execSQL("UPDATE tbl_books SET " +
                            "book_title='" + title + "', " +
                            "book_author='" + author + "', " +
                            "book_isbn='" + isbn + "' " +
                            "WHERE book_id=" + Integer.parseInt(id));

                    displayMessage("Information!", "Book Record has been successfully modified");

                    updateBook.setText("UPDATE");
                    bookID.setEnabled(false);
                    refreshList();
                    clearInputs();
                } else {
                    displayMessage("Error", "Invalid Book ID");
                }

            }
        });
    }


}