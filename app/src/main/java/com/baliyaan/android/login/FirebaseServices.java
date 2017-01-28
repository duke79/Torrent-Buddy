package com.baliyaan.android.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.baliyaan.android.imdbtor.Event;
import com.baliyaan.android.imdbtor.MainActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class FirebaseServices {
    // Singleton
    private static FirebaseServices instance = null;
    public static FirebaseServices getInstance(Context context, User user, Bus bus){
        if(instance==null){
            instance = new FirebaseServices(context,user,bus);
        }
        return instance;
    }

    User mUser;
    private Bus mBus = null;
    private static final String TAG = FirebaseServices.class.getSimpleName();

    Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private FirebaseServices(Context context, User user, Bus bus){
        mContext = context;
        mUser = user;
        mBus = bus;
        mBus.register(this);

        if (mAuth != null)
            mAuth.addAuthStateListener(mAuthListener);
        InitializeFirebase();
        StartTrackingFirebaseLogInOut();
    }

    private void InitializeFirebase() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set listener for Auth State changed
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    OnFirebaseLogIn();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void StartTrackingFirebaseLogInOut() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    OnFirebaseLogOut();
                }
            }
        };
        accessTokenTracker.startTracking();
    }

    private void OnFirebaseLogIn() {
        Profile profile = Profile.getCurrentProfile();
        if (profile == null) return;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) return;
        StoreOnFirebase();
    }

    @Subscribe
    public void StoreOnFirebase(Event.StoreOnFirebase event){
        StoreOnFirebase();
    }

    private void StoreOnFirebase() {
        if(mUser==null) return;

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

        // Creating new user node, which returns the unique key value
        // new user node would be /users/$userid/
        //String userId = mDatabase.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference user = users.child(userId);
        String usreString = user.toString();

        // pushing user to 'users' node using the userId
        users.child(userId).setValue(mUser);

        if(mUser.GetFBWantsToWatchList().size()>0)
            MainActivity.bus.post(mUser.GetFBWantsToWatchList());
    }

    private void OnFirebaseLogOut() {
        FirebaseAuth.getInstance().signOut();
    }

    //TODO: Invite friends

    public  void integrateWithFB(AccessToken token) {
        Log.d(TAG, "IntegrateFirebaseFBwithFBtoken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        OnFirebaseFBintegrationSuccess(task);
                    }
                });
    }

    private void OnFirebaseFBintegrationSuccess(Task<AuthResult> task) {
        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
        if (!task.isSuccessful()) {
            Log.w(TAG, "signInWithCredential", task.getException());
            Toast.makeText(mContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
