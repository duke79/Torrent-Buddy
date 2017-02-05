package com.baliyaan.android.torrents;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.baliyaan.android.imdbtor.R;

import java.io.InputStream;
import java.util.ArrayList;


public class BackgroundService extends IntentService {
    //Actions
    public static final String ACTION_FIND_TORRENT = "com.baliyaan.android.torrents.action.FIND_TORRENT";
    //Params
    public static final String NameList = "com.baliyaan.android.torrents.extra.Name_List";
    private String TAG = "TorrentsService";

    // Constructor
    public BackgroundService() {
        super("BackgroundService");
    }

    // Intent receiver
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FIND_TORRENT.equals(action)) {
                // Start service in foreground
                //startForeground(1,buildForegroundNotification());

                // Try to find torrents
                final ArrayList<String> nameList = intent.getStringArrayListExtra(NameList);
                findTorrents(nameList);

                // Stop service
                //stopForeground(true);
            }
        }
    }

    // Intent handler
    private void findTorrents(ArrayList<String> nameList) {
        Log.d(TAG,"Searching for torrents...");

        ArrayList<Torrent> torrentsAll = new ArrayList<>();
        for(int i=0;i<nameList.size();i++)
        {
            InputStream inputStream = this.getResources().openRawResource(R.raw.meta);
            ArrayList<Torrent> torrents = Services.GetTorrents(nameList.get(i),inputStream);
            torrentsAll.addAll(torrents);
        }
    }

    // Build foreground notification
    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true);

        b.setContentTitle("IMBDTor")
                .setContentText("Fetching torrents")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("");

        return(b.build());
    }

}
