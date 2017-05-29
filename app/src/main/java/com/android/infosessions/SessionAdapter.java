package com.android.infosessions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thao on 5/4/17.
 */

public class SessionAdapter extends ArrayAdapter<Session> {

    public SessionAdapter(Context context, ArrayList<Session> sessions) {
        super(context, 0, sessions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_session, parent, false);
        }
        final Session currentSession = getItem(position);

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.employer);
        nameTextView.setText(currentSession.getEmployer());

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);
        timeTextView.setText(currentSession.getStartTime());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        dateTextView.setText(currentSession.getDate());

        TextView detailTextView = (TextView) listItemView.findViewById(R.id.description);
        detailTextView.setText(currentSession.getDescription());

        TextView locationTextView = (TextView) listItemView.findViewById(R.id.location);
        locationTextView.setText(currentSession.getBuildingCode());

        ImageView logoView = (ImageView) listItemView.findViewById(R.id.employer_logo);
        Drawable drawable = getImage(logoView.getContext(), currentSession.getLogoString());

        logoView.setImageDrawable(drawable);

        return listItemView;
    }
    public static Drawable getImage(Context context, String name) {
        Drawable drawable = context.getResources().getDrawable(context.getResources()
                .getIdentifier(name, "drawable", context.getPackageName()));
        return drawable;
    }
}
