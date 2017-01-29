package com.baliyaan.android.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class FB {
    // Singleton
    private static FB instance = null;
    public static FB getInstance(Context context, LoginButton fbLoginButton, User user, Bus bus){
        if(instance==null){
            instance = new FB(context,fbLoginButton,user,bus);
        }
        return instance;
    }

    private User mUser;
    private Context mContext;
    private AccessToken mAccessToken;
    private static final String TAG = FB.class.getSimpleName();
    private CallbackManager mCallbackManager;
    private Bus mBus;


    private FB(Context context, LoginButton fbLoginButton, User user, Bus bus) {
        mContext = context;
        mUser = user;
        mBus = bus;

        FacebookSdk.sdkInitialize(context);
        InitializeFBLoginButton(fbLoginButton);
        IsFBLoggedIn();
        RequestFBBasicUserInfo();
        RequestFBWantsToWatchList();
        RequestFBFriendsWhoUseThisApp();
    }

    public boolean IsFBLoggedIn() {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Log.d(TAG, "Logged in User: " + profile.getFirstName() + " " + profile.getLastName());
        } else {
            ShowHashKey(mContext,"\"com.baliyaan.android.imdbtor\"");
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void ShowHashKey(Context context, String packageStr) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageStr,
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("KeyHash:", sign);
                //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
            Log.d("KeyHash:", "****------------***");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public AccessToken getAccessToken(){
        return mAccessToken;
    }

    private void InitializeFBLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions("email", "public_profile", "user_actions.video", "user_friends");

        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                OnFBLoginSuccess(loginResult);
                // Publish
                Event.Facebook.Login.Success event = new Event.Facebook.Login.Success();
                event.token = loginResult.getAccessToken();
                mBus.post(event);
            }

            @Override
            public void onCancel() {
                OnFBLoginCancel();
                // Publish
                mBus.post(new Event.Facebook.Login.Cancel());
            }

            @Override
            public void onError(FacebookException error) {
                OnFBLoginError(error);
                // Publish
                mBus.post(new Event.Facebook.Login.Error());
            }
        });
    }

    private void OnFBLoginSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook:onSuccess:" + loginResult);
        mAccessToken = loginResult.getAccessToken();
    }

    private void OnFBLoginCancel() {
        Log.d(TAG, "facebook:onCancel");
    }

    private void OnFBLoginError(FacebookException error) {
        Log.d(TAG, "facebook:onError", error);
    }

    private void RequestFBBasicUserInfo() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                OnReturnedFBBasicUserInfo(object, response);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,cover,name,first_name,last_name,age_range,link,gender,locale,picture,timezone,updated_time,verified,email");//,user_friends");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void OnReturnedFBBasicUserInfo(JSONObject object, GraphResponse response) {
        FacebookRequestError error = response.getError();
        if (object == null) return;
        try {
            mUser.fb_id = (String) object.get("id");
            mUser.fb_cover_url = (String) object.getJSONObject("cover").get("source");
            mUser.fb_username = (String) object.get("name");
            mUser.fb_f_name = (String) object.get("first_name");
            mUser.fb_l_name = (String) object.get("last_name");
            mUser.fb_age_range_min = (int) object.getJSONObject("age_range").get("min");
            mUser.fb_link = (String) object.get("link");
            mUser.fb_gender = (String) object.get("gender");
            mUser.fb_locale = (String) object.get("locale");
            mUser.fb_picture_url = (String) object.getJSONObject("picture").getJSONObject("data").get("url");
            mUser.fb_timezone = (double) object.get("timezone");
            mUser.fb_updated_time = (String) object.get("updated_time");
            mUser.fb_verified = (boolean) object.get("verified");
            //mUser.fb_email = (String) object.get("email");
            //mUser.fb_appFriends = object.getJSONArray("user_friends");
            Log.d(TAG, object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Publish
        Event.Facebook.DataReturned.UserInfo event = new Event.Facebook.DataReturned.UserInfo();
        mBus.post(event);
    }

    private void RequestFBWantsToWatchList() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/video.wants_to_watch",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        OnReturnedFBWantsToWatchList(response);
                    }
                }
        ).executeAsync();
    }

    private void OnReturnedFBWantsToWatchList(GraphResponse response) {
        if (response == null) return;
        FacebookRequestError error = response.getError();
        Log.d(TAG, response.toString());
        JSONObject obj = response.getJSONObject();
        if(obj==null)return;
        try {
            JSONArray videosList = (JSONArray) obj.get("data");
            for (int i = 0; i < videosList.length(); i++) {
                JSONObject video = videosList.getJSONObject(i);
                String title = (String) video.getJSONObject("data").getJSONObject("movie").get("title");
                Log.d(TAG, title.toString());
            }
            mUser.fb_wantsToWatchList = videosList.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Publish
        Event.Facebook.DataReturned.WantsToWatchList event = new Event.Facebook.DataReturned.WantsToWatchList();
        event.videos = mUser.GetFBWantsToWatchList();
        mBus.post(event);
    }

    private void RequestFBFriendsWhoUseThisApp() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        OnReturnedFBFriendsWhoUseThisApp(response);
                    }
                }
        ).executeAsync();
    }

    private void OnReturnedFBFriendsWhoUseThisApp(GraphResponse response) {
        if (response == null) return;
        FacebookRequestError error = response.getError();
        Log.d(TAG, response.toString());
        JSONObject obj = response.getJSONObject();
        if(obj==null)return;
        try {
            JSONArray friendsList = (JSONArray) obj.get("data");
            mUser.fb_appFriends = friendsList.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Publish
        com.baliyaan.android.login.Event.Facebook.DataReturned.FriendsWhoUseThisApp event = new com.baliyaan.android.login.Event.Facebook.DataReturned.FriendsWhoUseThisApp();
        mBus.post(event);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackManager == null) return;
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
