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

    public static class FacebookEvent extends Event{
        public static class Success extends FacebookEvent{
            public AccessToken token = null;
        }
        public static class Cancel extends FacebookEvent{
        }
        public static class Error extends FacebookEvent{
        }
    }

    public static class StoreOnFirebase extends Event{
    }
}
