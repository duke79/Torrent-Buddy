package com.baliyaan.android.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.baliyaan.android.imdbtor.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class Services {
    // Singleton
    private static Services instance = null;
    public static Services getInstance(Context context, View fbLoginButton,Bus bus){
        if(instance==null){
            instance = new Services(context,fbLoginButton,bus);
        }
        return instance;
    }

    private FBServices mFBServices;
    private FirebaseServices mFirebaseServices;
    private User mUser;
    private Bus mBus;

    private Services(Context context, View fbLoginButton, @Nullable Bus bus){
        mUser = new User();
        if(bus==null) bus = new Bus();
        mBus = bus;
        mBus.register(this);
        mFBServices = FBServices.getInstance(context,fbLoginButton,mUser,mBus);
        mFirebaseServices = FirebaseServices.getInstance(context,mUser,mBus);
    }

    @Subscribe
    public void OnFBLogin(Event.FacebookEvent.Success event){
        firebase().integrateWithFB(event.token);
    }

    public FBServices fb(){
        return mFBServices;
    }

    public FirebaseServices firebase(){
        return mFirebaseServices;
    }

    public User user(){
        return mUser;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fb().onActivityResult(requestCode,resultCode,data);
    }
}
