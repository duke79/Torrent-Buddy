package com.baliyaan.android.login;

import com.facebook.AccessToken;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/29/2017.
 */

public class Event {
    public static class Facebook extends com.baliyaan.android.login.Event {
        public static class Login extends Facebook{
            public static class Success extends Login {
                public AccessToken token = null;
            }

            public static class Cancel extends Login {
            }

            public static class Error extends Login {
            }
        }

        public static class DataReturned extends Facebook{
            public static class WantsToWatchList extends DataReturned{
                public ArrayList<String> videos = new ArrayList<>();
            }
            public static class UserInfo extends DataReturned{
            }
            public static class FriendsWhoUseThisApp extends DataReturned{
            }
        }
    }

    public static class User extends Event{
        public static class VideoListUpdated extends User{
            public ArrayList<String> videos = new ArrayList<>();
        }
    }
}
