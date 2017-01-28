package com.baliyaan.android.torrents;

import android.content.Context;

import com.baliyaan.android.imdbtor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Services {
    public static ArrayList<Provider> GetProvidersList(Context context) {
        ArrayList<Provider> providers = new ArrayList<>();

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

        Provider provider = null;
        try {
            JSONObject jsonFile = new JSONObject(jsonStream);
            JSONArray jsonProvidersList = jsonFile.getJSONArray("providers");
            for (int i = 0; i < jsonProvidersList.length(); i++) {
                try {
                    provider = new Provider();
                    JSONObject jsonProvider = jsonProvidersList.getJSONObject(i);
                    provider.title = jsonProvider.optString("name");
                    provider.searchURL = jsonProvider.optString("searchUrl");
                    provider.icon = jsonProvider.optString("icon");

                    JSONObject jsonSelectors = jsonProvider.getJSONObject("cssSelectors");
                    provider.magnetsSelector = jsonSelectors.optString("magnets");
                    provider.titlesSelector = jsonSelectors.optString("titles");
                    provider.timestampsSelector = jsonSelectors.optString("timestamps");
                    provider.sizesSelector = jsonSelectors.optString("titles");
                    provider.seedsSelector = jsonSelectors.optString("seeds");
                    provider.leechesSelector = jsonSelectors.optString("leeches");
                    provider.URLsSelector = jsonSelectors.optString("URLs");
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

        ArrayList<Provider> providers = GetProvidersList(context);
        for(int i=0;i<providers.size();i++)
        {
            torrents.addAll(providers.get(i).GetTorrents(q));
        }

        return torrents;
    }
}