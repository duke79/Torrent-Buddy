package com.baliyaan.android.imdbtor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RC_SIGN_IN = 123;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private static final String TAG = LoginFragment.class.getSimpleName();
    CallbackManager mCallbackManager;
    User mUser = new User();

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            if (mAuth != null)
                mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        InitializeFirebase();
    }

    private void InitializeFirebase() {
        // Disk persistence for fire-base database (makes it work offline)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SDK must be initialized before inflating LoginButton
        FacebookSdk.sdkInitialize(getContext());
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        StartTrackingFirebaseLogInOut();
        InitializeFBLoginButton(view);
        IsFBLoggedIn();
        return view;
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
        RequestFBBasicUserInfo(accessToken);
        RequestFBWantsToWatchList();
        RequestFBFriendsWhoUseThisApp();
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
        mListener.onUserInfoUpdated(mUser.GetFBWantsToWatchList());
    }

    private void OnFirebaseLogOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private void InitializeFBLoginButton(View view) {
        final LoginButton loginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_actions.video", "user_friends");

        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                OnFBLoginSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                OnFBLoginCancel();
            }

            @Override
            public void onError(FacebookException error) {
                OnFBLoginError(error);
            }
        });
    }

    private void OnFBLoginSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook:onSuccess:" + loginResult);
        IntegrateFirebaseFBwithFBtoken(loginResult.getAccessToken());
    }

    private void OnFBLoginCancel() {
        Log.d(TAG, "facebook:onCancel");
    }

    private void OnFBLoginError(FacebookException error) {
        Log.d(TAG, "facebook:onError", error);
        Toast.makeText(getContext(), R.string.FBLoginError, Toast.LENGTH_SHORT).show();
    }

    private void RequestFBBasicUserInfo(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
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
        StoreOnFirebase();
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
        StoreOnFirebase();
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
        StoreOnFirebase();
    }

    //TODO: Invite friends

    private void IntegrateFirebaseFBwithFBtoken(AccessToken token) {
        Log.d(TAG, "IntegrateFirebaseFBwithFBtoken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
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
            Toast.makeText(getContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean IsFBLoggedIn() {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Log.d(TAG, "Logged in User: " + profile.getFirstName() + " " + profile.getLastName());
        } else {
            ShowHashKey(getContext());
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void ShowHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.baliyaan.android.imdbtor",
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager == null) return;
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUserInfoUpdated(ArrayList<String> videosList);
    }
}
