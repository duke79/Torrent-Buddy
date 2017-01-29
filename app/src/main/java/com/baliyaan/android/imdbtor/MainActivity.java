package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.baliyaan.android.afsm.Action;
import com.baliyaan.android.afsm.Condition;
import com.baliyaan.android.afsm.FSM;
import com.baliyaan.android.afsm.Transition;
import com.baliyaan.android.login.Services;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class MainActivity
        extends AppCompatActivity
        implements SearchResultsFragment.OnFragmentInteractionListener {

    public Context mContext;
    public String TAG = MainActivity.class.getSimpleName();

    public static Bus bus = new Bus(ThreadEnforcer.ANY);

    SearchResultsFragment mSearchResultsFragment = null;
    private View mVideosView = null;
    private LoginButton mFBLoginView = null;
    private Services mLoginServices = null;
    private View mHomePage = null;
    private View mSearchTorrentsPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        bus.register(this);

        // Disk persistence for fire-base database (makes it work offline)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // Crash if re-called due to a fragment

        InitializeFSM();
    }

    private void InitializeFSM() {
        FSM.addStates(new String[]{
                "Root", "LoginPrompt", "SearchResults"
        });

        new Transition("Root", "", "LoginPrompt")
                .setAction(new Action() {
                    @Override
                    public void run(Object data) {
                        setupVideoListView();
                        setupLogin();
                        setupTopBar();
                        mHomePage = findViewById(R.id.HomePage);
                        mSearchTorrentsPage = findViewById(R.id.SearchTorrentsPage);
                    }
                });

        new Transition("LoginPrompt", "", "SearchResults")
                .setAction(new Action() {
                    @Override
                    public void run(Object data) {
                        if (data == null) return;
                        try {
                            Event.SearchTorrent searchTorrent = (Event.SearchTorrent) data;
                            if (searchTorrent == null) return;
                            Handler handler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    //mFBLoginView.setVisibility(View.GONE);
                                    //mVideosView.setVisibility(View.GONE);
                                    mHomePage.setVisibility(View.GONE);
                                    mSearchTorrentsPage.setVisibility(View.VISIBLE);
                                }
                            };
                            handler.post(myRunnable);
                            StartSearch(searchTorrent.query);
                        } catch (ClassCastException e) {
                            Log.e(TAG, "Invalid SearchTorrent object");
                        }
                    }
                })
        .setCondition(new Condition() {
            @Override
            public boolean isGo(Object data) {
                if (data == null) return false;
                try {
                    Event.SearchTorrent searchTorrent = (Event.SearchTorrent) data;
                    if (searchTorrent == null) return false;
                    return true;
                }catch (ClassCastException e){
                    Log.e(TAG,"Invalid SearchTorrent object");
                }
                return false;
            }
        });

        new Transition("SearchResults", "", "LoginPrompt")
                .setAction(new Action() {
                    @Override
                    public void run(Object data) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                //mFBLoginView.setVisibility(View.VISIBLE);
                                //mVideosView.setVisibility(View.VISIBLE);
                                mHomePage.setVisibility(View.VISIBLE);
                                mSearchTorrentsPage.setVisibility(View.GONE);
                            }
                        };
                        handler.post(myRunnable);
                    }
                })
                .setCondition(new Condition() {
                    @Override
                    public boolean isGo(Object data) {
                        if (data == null) return false;
                        try {
                            Event.BackPressed backPressed = (Event.BackPressed) data;
                            if (backPressed == null) return false;
                            return true;
                        }catch (ClassCastException e){
                            Log.e(TAG,"Invalid BackPressed object");
                        }
                        return false;
                    }
                });
        FSM.transit(null);
    }


    @Subscribe
    public void OnEvent(Object event){
        FSM.transit(event);
    }

    @Override
    public void onBackPressed() {
        Event.BackPressed backPressedEvent = new Event.BackPressed();
        bus.post(backPressedEvent);
    }

    private void setupLogin() {
        mFBLoginView = (LoginButton) findViewById(R.id.fb_login_button);
        String packageStr = "com.baliyaan.android.imdbtor";
        mLoginServices = Services.getInstance(mContext,mFBLoginView,bus,packageStr);
    }

    private void setupVideoListView() {
        mVideosView = findViewById(R.id.Videos);
        new VideoListPresenter(this, mVideosView);
    }

    private void setupTopBar() {
        View searchIcon = findViewById(R.id.menu_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new Event.SearchTorrent());
            }
        });
    }

    public void StartSearch(String query) {
        SearchView searchView = (SearchView) mSearchTorrentsPage.findViewById(R.id.SearchBox);
        //searchView.setQuery(query,false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginServices.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Required only for communication among fragments
    }
}
