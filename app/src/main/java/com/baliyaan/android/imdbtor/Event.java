package com.baliyaan.android.imdbtor;

import com.facebook.AccessToken;

/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class Event {
    public Event(){
    }

    public static class BackPressed extends Event{
    }

    public static class SearchTorrent extends Event{
        public String query = "";
    }

}
