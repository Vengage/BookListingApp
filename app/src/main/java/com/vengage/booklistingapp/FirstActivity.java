package com.vengage.booklistingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.vengage.booklistingapp.Utils.getStringFromEditable;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //  Start the BookListingActivity with the entered string
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
                String query = getStringFromEditable(searchEditText.getText());
                if (query.equalsIgnoreCase("")) {
                    Toast.makeText(FirstActivity.this, "Enter a word in the search box.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent bookListingIntent = new Intent(FirstActivity.this, BookListingActivity.class);
                    bookListingIntent.putExtra("queryString", query);
                    startActivity(bookListingIntent);
                }
            }
        });
    }
}
