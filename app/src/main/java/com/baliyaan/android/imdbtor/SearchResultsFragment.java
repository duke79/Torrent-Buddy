package com.baliyaan.android.imdbtor;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.baliyaan.android.torrents.Torrent;
import com.baliyaan.android.torrents.Services;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ListViewCompat mResultsList = null;
    ResultListAdapter mResultListAdapter = null;
    ArrayList<Torrent> mTorrents = new ArrayList<>();
    private SearchView mSearchView;
    private Activity mActivity;
    private Handler mHandler;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    String mQuery = "Ubuntu";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultsFragment newInstance(String param1, String param2) {
        SearchResultsFragment fragment = new SearchResultsFragment();
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
    }

    private void setupSearchView(View view) {
        mSearchView = (SearchView) view.findViewById(R.id.SearchBox);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                assert mSearchView != null;
                MakeSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    private void MakeSearch(final String query) {
        mQuery = query;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTorrents.clear();
                final ArrayList<Torrent> torrents = Services.GetTorrents(mActivity, query);
                int size = mTorrents.size();
                if(mHandler==null)
                    mHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTorrents.addAll(torrents);
                        mResultListAdapter.notifyDataSetChanged();
                    }
                };
                mHandler.post(myRunnable);
            }
        }).start();
    }

    private void setupResultsList(View view) {
        mResultsList = (ListViewCompat) view.findViewById(R.id.Results);
        mResultListAdapter = new ResultListAdapter(mActivity, mTorrents);
        mResultsList.setAdapter(mResultListAdapter);
        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Torrent torrent = (Torrent) mResultListAdapter.getItem(position);
                Toast.makeText(mActivity, torrent.title, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mQuery!=null)
        {
            if(mQuery.length()>0)
            {
                mSearchView.setQuery(mQuery,false);
                MakeSearch(mQuery);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mQuery!=null)
        {
            mQuery="";
            mSearchView.setQuery(mQuery,true);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mTorrents!=null) {
                        mTorrents.clear();
                        mResultListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        setupBackButton(view);
        setupSearchView(view);
        setupResultsList(view);
        return view;
    }

    private void setupBackButton(View view) {
        ImageView backBtn = (ImageView) view.findViewById(R.id.back_icon);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        ((AppCompatActivity)mActivity).getSupportActionBar().hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ((AppCompatActivity)mActivity).getSupportActionBar().show();
        mActivity = null;
    }

    public void SearchOnStart(final String query) {
        mQuery = query;
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
        void onFragmentInteraction(Uri uri);
    }
}
