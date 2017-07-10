package com.android.infosessions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by Thao on 7/9/17.
 */

public class FetchLogos {
    public static Drawable getImage(Context context, String name) {
        Drawable drawable = context.getResources().getDrawable(context.getResources()
                .getIdentifier(name, "drawable", context.getPackageName()));
        return drawable;
    }

    public static String getEmployerLogo(String str) {
        ArrayList<String> logos = new ArrayList<>();
        logos.add("a500px");
        logos.add("a9");
        logos.add("arista");
        logos.add("at_t");
        logos.add("autodesk");
        logos.add("bazaarvoice");
        logos.add("bloomberg");
        logos.add("cibc");
        logos.add("dacgroup");
        logos.add("digiflare");
        logos.add("electronic_arts");
        logos.add("facebook");
        logos.add("genesys");
        logos.add("google");
        logos.add("group_by");
        logos.add("hootsuite");
        logos.add("infusion");
        logos.add("league_inc");
        logos.add("loblaw_digital");
        logos.add("meraki");
        logos.add("microsoft");
        logos.add("pointclickcare");
        logos.add("rbc");
        logos.add("redfin");
        logos.add("td");
        logos.add("tribalscale");
        logos.add("twitter");
        logos.add("uber");
        logos.add("ukengame");
        logos.add("wave_accounting");
        logos.add("whatsapp");
        logos.add("yelp");
        logos.add("yext");
        String name = str;
        name = name.replace(" ", "_");
        name = name.toLowerCase();
        boolean replaced = false;
        for(int i = 0; i < logos.size(); i++) {
            if(name.compareToIgnoreCase(logos.get(i)) == 0 || name.contains(logos.get(i)) || logos.get(i).contains(name)) {
                name = logos.get(i);
                replaced = true;
                break;
            }
        }
        if(!replaced) {
            name = "nonlogo";
        }
        return name;
    }
}
