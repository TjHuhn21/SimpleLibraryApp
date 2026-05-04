package com.example.agagonataydelibrarysystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText bookDetails, bookID;

    ListView bookList;
    Button addBook, viewBook, updateBook, deleteBook, clearBook;


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
    }
    public void init(){

        bookDetails = findViewById(R.id.etBookDetail);
        bookID = findViewById(R.id.etBookID);
        bookList = findViewById(R.id.lvBookList);
        addBook = findViewById(R.id.btnAdd);
        viewBook = findViewById(R.id.btnView);
        updateBook = findViewById(R.id.btnUpdate);
        deleteBook = findViewById(R.id.btnDelete);
        clearBook = findViewById(R.id.btnClear);

    }
}