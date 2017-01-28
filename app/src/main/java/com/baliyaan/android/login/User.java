package com.baliyaan.android.login;

import android.support.annotation.Keep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/18/2017.
 */

@Keep
public class User {
    public String fb_id;
    public String fb_cover_url;
    public String fb_username;
    public int fb_age_range_min;
    public String fb_f_name;
    public String fb_l_name;
    public String fb_link;
    public String fb_gender;
    public String fb_locale;
    public String fb_picture_url;
    public double fb_timezone;
    public String fb_updated_time;
    public boolean fb_verified;
    public String fb_appFriends;
    public String fb_email;
    public String fb_wantsToWatchList;
    private static User mUser;

    public User(){
        mUser = this;
    }

    public ArrayList<String> GetFBWantsToWatchList(){
        ArrayList<String> arrayList = new ArrayList<>();
        if(fb_wantsToWatchList == null)
            return arrayList;
        try {
            JSONArray jsonArray = new JSONArray(fb_wantsToWatchList);
            for(int i=0;i<jsonArray.length();i++)
            {
                String title = ((JSONObject)jsonArray.get(i)).getJSONObject("data").getJSONObject("movie").get("title").toString();
                arrayList.add(title);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static User GetUser()
    {
        return mUser;
    }
}
