package com.vengage.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookListingActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>>, SearchView.OnQueryTextListener,
        SearchView.OnFocusChangeListener {

    // Debugging tag
    private static final String TAG = "BookListingActivity";

    // The ID of the loader
    private static final int BOOK_LISTING_ID = 1;

    // ListView for books
    ListView booksListView;
    // Adapter for the list of books
    private BookAdapter bookAdapter;
    // Empty view to display messages when data is not present
    private TextView emptyView;
    // Progress bar for loading books data
    private ProgressBar progressBar;
    // LoaderManager
    private LoaderManager loaderManager = null;
    // SearchView
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        // Init the loader
        if (loaderManager == null)
            loaderManager = getSupportLoaderManager();

        booksListView = (ListView) findViewById(R.id.booksList);

        // Empty view
        emptyView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyView);

        // Progress bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // The adapter for the list of books
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter to the list
        booksListView.setAdapter(bookAdapter);

        // Send the user to the book's webpage when onClick()
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // First we verify that we have Internet connection
                if (checkNetworkConnectivity()) {
                    // Get the current book from the adapter
                    Book currentBook = bookAdapter.getItem(position);

                    // Get the infoLink from the book and parse it into an Uri
                    Uri bookPreviewUri = Uri.parse(currentBook.getmInfoLink());

                    // Create the intent to view the book Uri
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookPreviewUri);

                    // Start the activity by sending the intent
                    startActivity(websiteIntent);
                } else {
                    Toast.makeText(BookListingActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (checkNetworkConnectivity()) {
            Intent intent = getIntent();
            String query = intent.getStringExtra("queryString");
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            loaderManager.initLoader(BOOK_LISTING_ID, bundle, this).forceLoad();
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        String query = args.getString("query");
        return new BookLoader(BookListingActivity.this, getString(R.string.google_request_url, query));
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Clear the previous books
        bookAdapter.clear();

        // After the data has finished loading
        progressBar.setVisibility(View.GONE);

        //  Check if we have data
        if (data == null || data.size() < 1) {
            emptyView.setText(R.string.no_books_found);
            return;
        }

        // Add the new list of books. Trigger ListView update.
        bookAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // onLoaderReset() clear all of the previous data
        bookAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Asociate the SearchView with the OnQueryTextListener
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    /**
     * Function used to check for network connectivity
     *
     * @return Return true if is connected or false in other case
     */
    private boolean checkNetworkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Function that makes the query based on the string entered by the user
     *
     * @param query The string query entered by the user
     * @return Return if the function executed succesfully or not
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        // Check first if we have an empty new query
        if(query.isEmpty()){
            Toast.makeText(this, "The query is empty.", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Clear the old values
        bookAdapter.clear();

        // Perform the final search
        if (checkNetworkConnectivity()) {
            // Clear the focus for the query string
            searchView.clearFocus();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            loaderManager.restartLoader(BOOK_LISTING_ID, bundle, this).forceLoad();
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Check first if we have an empty new query
        if(newText.isEmpty()){
            return true;
        }

        // Clear the old values
        bookAdapter.clear();

        // Apply filtering
        if (checkNetworkConnectivity()) {
            Bundle bundle = new Bundle();
            bundle.putString("query", newText);
            loaderManager.restartLoader(BOOK_LISTING_ID, bundle, this).forceLoad();
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        }
        return true;
    }

    @Override
    public void onFocusChange(View view, boolean queryTextFocused) {
        if (!queryTextFocused) {
            String query = searchView.getQuery().toString();
            if (TextUtils.isEmpty(query))
                bookAdapter.clear();
        }
    }
}
