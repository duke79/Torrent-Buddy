package com.baliyaan.android.imdbtor;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 10/9/2016.
 */

public class WebScraper {
    public static Context mContext;

    // "http://stackoverflow.com/questions/2971155"
    public static Document GetDocument(String iURL) throws IOException {
        Document document = Jsoup.connect(iURL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .get();
        return document;
    }

    // "#question .post-text p"
    public static ArrayList<String> Select(Document iDocument, String iSelector) {
        ArrayList<String> selection = new ArrayList<String>();
        Elements elements = iDocument.select(iSelector);
        assert elements != null;
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String elemText = element.text();
            selection.add(elemText);
        }
        return selection;
    }

    public static void main(String[] args) {
        ArrayList<Torrent> torrents = TorrentProviderServices.GetTorrents(mContext,"the mart");
    }

    // TODO: Works only for windows. May need something else for Android.
    // See: http://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
    private static void OpenMagnetInBrowser(String uri) {
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("rundll32 url.dll,FileProtocolHandler "+uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> GetListOfMovies() {
        Document document = null;
        try {
            document = GetDocument("https://www.imdb.com/user/ur27429396/watchlist?ref_=wt_nv_wl_all_0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> movies = Select(document, "#main > div > span > #center-1-react > div > div:nth-child(3) > div.lister-list.mode-detail > #page-1 > div > div > div.lister-item-content > h3 > a");
        return movies;
    }

}