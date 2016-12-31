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
            JSONObject jsonObject = new JSONObject(jsonStream);
            JSONArray jsArray = jsonObject.getJSONArray("providers");
            for (int i = 0; i < jsArray.length(); i++) {
                try {
                    provider = new TorrentProvider();
                    JSONObject jsObject = jsArray.getJSONObject(i);
                    provider.title = jsObject.optString("name");
                    provider.searchURL = jsonObject.optString("searchUrl");
                    provider.magnetsSelector = jsObject.optString("magnetsSelector");
                    provider.titlesSelector = jsObject.optString("titlesSelector");
                    provider.timestampsSelector = jsObject.optString("timestampsSelector");
                    provider.sizesSelector = jsObject.optString("titlesSelector");
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
        ArrayList<Torrent> torrents = null;

        ArrayList<TorrentProvider> providers = GetProvidersList(context);
        for(int i=0;i<providers.size();i++)
        {
            torrents.addAll(providers.get(i).GetTorrents(q));
        }

        return torrents;
    }
}