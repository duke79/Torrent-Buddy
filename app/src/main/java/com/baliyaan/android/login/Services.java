package com.baliyaan.android.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.facebook.login.widget.LoginButton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class Services {
    // Singleton
    private static Services instance = null;
    public static Services getInstance(Context context, LoginButton fbLoginButton,Bus bus){
        if(instance==null){
            instance = new Services(context,fbLoginButton,bus);
        }
        return instance;
    }

    private FB mFB;
    private Firebase mFirebase;
    private User mUser;
    private Bus mBus;

    private Services(Context context, LoginButton fbLoginButton, @Nullable Bus bus){
        mUser = new User();
        if(bus==null) bus = new Bus();
        mBus = bus;
        mBus.register(this);
        mFB = FB.getInstance(context,fbLoginButton,mUser,mBus);
        mFirebase = Firebase.getInstance(context,mUser,mBus);
    }

    @Subscribe
    public void OnFBLogin(Event.Facebook.Login.Success event){
        firebase().integrateWithFB(event.token);
    }

    public FB fb(){
        return mFB;
    }

    public Firebase firebase(){
        return mFirebase;
    }

    public User user(){
        return mUser;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fb().onActivityResult(requestCode,resultCode,data);
    }

    @Subscribe
    public void OnFBDataReturned(Event.Facebook.DataReturned event){
        firebase().UpdateFirebaseServer();
        if(mUser.GetFBWantsToWatchList().size()>0) {
            Event.User.VideoListUpdated videoListEvent = new Event.User.VideoListUpdated();
            videoListEvent.videos = mUser.GetFBWantsToWatchList();
            mBus.post(videoListEvent);
        }
    }
}
