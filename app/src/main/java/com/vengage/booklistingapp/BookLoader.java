package com.vengage.booklistingapp;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Vengage on 7/4/2017.
 *
 * Loader for getting books from Google Books API
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    // Request URL
    private String GOOGLE_BOOKS_REQUEST_URL;

    public BookLoader(Context context, String google_books_request_url) {
        super(context);
        GOOGLE_BOOKS_REQUEST_URL = google_books_request_url;
    }

    @Override
    public List<Book> loadInBackground() {
        // The list of books that is load on another thread
        List<Book> books = Utils.fetchBooksData(GOOGLE_BOOKS_REQUEST_URL);

        return books;
    }
}
