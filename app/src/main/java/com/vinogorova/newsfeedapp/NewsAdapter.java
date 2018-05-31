package com.vinogorova.newsfeedapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    public NewsAdapter(@NonNull Context context, int resource, List<Article> newsFeedList) {
        super(context, resource, newsFeedList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        //Get current Article object from the List of articles
        Article currentArticle = (Article) getItem(position);
        Log.e(LOG_TAG, "Current Article: " + currentArticle.getTitle());

        // Find the TextView with view ID article_title
        // And set data from the current Article object
        TextView title = convertView.findViewById(R.id.article_title);
        title.setText(currentArticle.getTitle());

        // Find the TextView with view ID article_date
        // And set data from the current Article object
        TextView date = convertView.findViewById(R.id.article_date);
        date.setText(currentArticle.getDate());

        // Find the TextView with view ID article_section
        // And set data from the current Article object
        TextView section = convertView.findViewById(R.id.article_section);
        section.setText(currentArticle.getSection());

        // Find the TextView with view ID article_author
        // And set data from the current Article object
        TextView author = convertView.findViewById(R.id.article_author);
        if (currentArticle.getAuthor() == null) {
            Log.e(LOG_TAG, "Author string is empty");
            author.setVisibility(View.GONE);
        } else {
            String name = authorNameHandler(currentArticle.getAuthor());
            author.setText(name);
        }

        return convertView;
    }

    /**
     * Removes unnecessary characters from the given string
     * @param authorName
     * @return authorName
     */
    private static String authorNameHandler (String authorName){
        if (authorName.contains("-")){
            Log.e(LOG_TAG, "Author contains -");
            int index = authorName.indexOf("-");
            StringBuilder sb = new StringBuilder(authorName);
            sb.deleteCharAt(index);
            sb.insert(index, " ");
            authorName = sb.toString();
        }
        return authorName;
    }
}
