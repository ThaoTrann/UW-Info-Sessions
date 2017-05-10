package com.android.infosessions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thao on 5/4/17.
 */

public class InfoAdapter extends ArrayAdapter<Info> {

    public InfoAdapter(Context context, ArrayList<Info> infos) {
        super(context, 0, infos);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        final Info currentInfo = getItem(position);

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
        nameTextView.setText(currentInfo.getName());

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);
        timeTextView.setText(currentInfo.getTime());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        dateTextView.setText(currentInfo.getDate());

        TextView detailTextView = (TextView) listItemView.findViewById(R.id.details);
        detailTextView.setText(currentInfo.getDetail());

        TextView locationTextView = (TextView) listItemView.findViewById(R.id.location);
        locationTextView.setText(currentInfo.getBuilding());

        ImageView logoView = (ImageView) listItemView.findViewById(R.id.employer_logo);
        Drawable drawable = getImage(logoView.getContext(), currentInfo.getLogoString());

        logoView.setImageDrawable(drawable);

        return listItemView;
    }
    public static Drawable getImage(Context context, String name) {
        Drawable drawable = context.getResources().getDrawable(context.getResources()
                .getIdentifier(name, "drawable", context.getPackageName()));
        return drawable;
    }
}
