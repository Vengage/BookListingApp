package com.vengage.booklistingapp;

import static com.vengage.booklistingapp.R.id.averageRating;

/**
 * Created by Vengage on 7/3/2017.
 * <p>
 * This is the class that describes a book
 */

class Book {

    // The title of the book
    private String mTitle;

    // The subtitle of the book
    private String mSubtitle;

    // The authors of the book
    private String mAuthors;

    // A short description of the book
    private String mDescription;

    // The HTML link to the book corresponding Google Books webpage
    private String mInfoLink;

    // A short menu_search text
    private String mSearchInfo;

    // Books page count
    private int mPageCount;

    // The number of ratings given by the users
    private int mRatingsCount;

    // The average rating of the book
    private double mAverageRating;

    // Default constructor
    Book(String mTitle, String mSubtitle, String mAuthors, String mDescription,
         String mInfoLink, String mSearchInfo, int mPageCount, int mRatingsCount,
         double mAverageRating) {
        this.mTitle = mTitle;
        this.mSubtitle = mSubtitle;
        this.mAuthors = mAuthors;
        this.mDescription = mDescription;
        this.mInfoLink = mInfoLink;
        this.mSearchInfo = mSearchInfo;
        this.mPageCount = mPageCount;
        this.mRatingsCount = mRatingsCount;
        this.mAverageRating = mAverageRating;
    }

    String getmTitle() {
        return mTitle;
    }

    String getmSubtitle() {
        return mSubtitle;
    }

    String getmAuthors() {
        return mAuthors;
    }

    String getmDescription() {
        return mDescription;
    }

    String getmInfoLink() {
        return mInfoLink;
    }

    String getmSearchInfo() {
        return mSearchInfo;
    }

    int getmPageCount() {
        return mPageCount;
    }

    int getmRatingsCount() {
        return mRatingsCount;
    }

    double getmAverageRating() {
        return mAverageRating;
    }
}
