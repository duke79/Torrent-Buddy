package com.baliyaan.android.imdbtor;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TorrentProviderServices {
    public static ArrayList<TorrentProvider> GetProvidersList(Context context) {
        ArrayList<TorrentProvider> providers = new ArrayList<>();

        InputStream inputStream = context.getResources().openRawResource(R.raw.meta);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonStream = stringBuilder.toString();

        TorrentProvider provider = null;
        try {
            JSONObject jsonFile = new JSONObject(jsonStream);
            JSONArray jsonProvidersList = jsonFile.getJSONArray("providers");
            for (int i = 0; i < jsonProvidersList.length(); i++) {
                try {
                    provider = new TorrentProvider();
                    JSONObject jsonProvider = jsonProvidersList.getJSONObject(i);
                    provider.title = jsonProvider.optString("name");
                    provider.searchURL = jsonProvider.optString("searchUrl");
                    provider.magnetsSelector = jsonProvider.optString("magnetsSelector");
                    provider.titlesSelector = jsonProvider.optString("titlesSelector");
                    provider.timestampsSelector = jsonProvider.optString("timestampsSelector");
                    provider.sizesSelector = jsonProvider.optString("titlesSelector");
                    provider.seedsSelector = jsonProvider.optString("seedsSelector");
                    provider.leechesSelector = jsonProvider.optString("leechesSelector");
                    providers.add(provider);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    providers.add(provider);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return providers;
    }

    public static ArrayList<Torrent> GetTorrents(Context context,String q) {
        ArrayList<Torrent> torrents = new ArrayList<>();

        ArrayList<TorrentProvider> providers = GetProvidersList(context);
        for(int i=0;i<providers.size();i++)
        {
            torrents.addAll(providers.get(i).GetTorrents(q));
        }

        return torrents;
    }
}
