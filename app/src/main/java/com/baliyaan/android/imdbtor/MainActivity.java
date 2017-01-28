package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.baliyaan.android.afsm.Action;
import com.baliyaan.android.afsm.Condition;
import com.baliyaan.android.afsm.FSM;
import com.baliyaan.android.afsm.Transition;
import com.baliyaan.android.login.LoginFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class MainActivity
        extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        SearchResultsFragment.OnFragmentInteractionListener {
    public Context mContext;
    public static Bus bus = new Bus(ThreadEnforcer.ANY);
    SearchView mSearchView = null;
    SearchResultsFragment mSearchResultsFragment = null;
    LoginFragment mLoginFragment = null;
    private View mVideosView;
    public String TAG = MainActivity.class.getSimpleName();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Event.SearchTorrent searchTorrentEvent = new Event.SearchTorrent();
                FSM.transit(searchTorrentEvent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

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
                        setupLoginFragment();
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
                                    mVideosView.setVisibility(View.GONE);
                                }
                            };
                            handler.post(myRunnable);
                            StartSearch(searchTorrent.query);
                        } catch (ClassCastException e) {
                            Log.e(TAG, "Invalid SearchTorrent object");
                        }
                    }
                });

        new Transition("SearchResults", "", "LoginPrompt")
                .setAction(new Action() {
                    @Override
                    public void run(Object data) {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();

                        if (mLoginFragment != null)
                            transaction.remove(mSearchResultsFragment);
                        Handler handler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                mVideosView.setVisibility(View.VISIBLE);
                            }
                        };
                        handler.post(myRunnable);
                        transaction.add(R.id.LoginFragmentContainer, mLoginFragment);
                        transaction.commit();
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

    private void setupVideoListView() {
        mVideosView = findViewById(R.id.Videos);
        new VideoListPresenter(this, mVideosView);
    }

    @Override
    public void onBackPressed() {
        Event.BackPressed backPressedEvent = new Event.BackPressed();
        FSM.transit(backPressedEvent);
    }

    private void setupLoginFragment() {
        if (mLoginFragment == null)
            mLoginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //if (mSearchResultsFragment != null)
        //  transaction.remove(mSearchResultsFragment);
        transaction.add(R.id.LoginFragmentContainer, mLoginFragment).commit();
    }

    public void StartSearch(String query) {
        if (mSearchResultsFragment == null) {
            mSearchResultsFragment = new SearchResultsFragment();
        }
        mSearchResultsFragment.SearchOnStart(query);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (mLoginFragment != null)
            transaction.remove(mLoginFragment);
        transaction.add(R.id.SearchResultsFragmentContainer, mSearchResultsFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
        //fm.executePendingTransactions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Required only for communication among fragments
    }
}
