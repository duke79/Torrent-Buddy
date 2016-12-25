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

/**
 * Created by Pulkit Singh on 12/25/2016.
 */

public class Provider {
    public String title;
    public String searchURL;
    public String magnetsSelector;
    public String titlesSelector;
    public String timestampsSelector;
    public String sizesSelector;

    public static ArrayList<Provider> getProvidersList(Context context) {
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
            JSONObject jsonObject = new JSONObject(jsonStream);
            JSONArray jsArray = jsonObject.getJSONArray("providers");
            for (int i = 0; i < jsArray.length(); i++) {
                try {
                    provider = new Provider();
                    JSONObject jsObject = jsArray.getJSONObject(i);
                    provider.title = jsObject.getString("name");
                    provider.magnetsSelector = jsObject.getString("magnetSelector");
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
}
