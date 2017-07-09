package com.vengage.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vengage on 7/4/2017.
 * <p>
 * Custom adapter for the Books list
 */

class BookAdapter extends ArrayAdapter<Book> {

    BookAdapter(@NonNull Context context, @NonNull ArrayList<Book> mBookList) {
        super(context, 0, mBookList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            viewHolder.authors = (TextView) convertView.findViewById(R.id.authors);
            viewHolder.averageRating = (TextView) convertView.findViewById(R.id.averageRating);
            viewHolder.ratingCount = (TextView) convertView.findViewById(R.id.ratingCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Book book = getItem(position);

        viewHolder.title.setText(book.getmTitle());
        viewHolder.subtitle.setText(book.getmSubtitle());
        viewHolder.authors.setText(book.getmAuthors());
        viewHolder.averageRating.setText(String.valueOf(book.getmAverageRating()));
        viewHolder.ratingCount.setText(String.valueOf(book.getmRatingsCount()));

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    private static class ViewHolder {
        TextView averageRating;
        TextView ratingCount;
        TextView title;
        TextView subtitle;
        TextView authors;
    }
}
