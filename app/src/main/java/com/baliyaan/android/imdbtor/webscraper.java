package com.baliyaan.android.imdbtor;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Pulkit Singh on 10/9/2016.
 */

public class webscraper {
    // "http://stackoverflow.com/questions/2971155"
    public static Document GetDocument(String iURL) throws IOException {
        Document document = Jsoup.connect(iURL).get();
        return document;
    }

    // "#question .post-text p"
    public static String Select(Document iDocument,String iSelector)
    {
        String selection = iDocument.select(iSelector).first().text();
        return selection;
    }

    public static void main(String[] args) {
        Log.d(TAG, "main: d");
    }

}