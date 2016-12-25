package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        //OpenMagnetInBrowser("magnet:?xt=urn:btih:20130aa512be804b52c6d751dfa7070e308037ce&dn=The.Martian.2015.HDRip.XviD-ETRG&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969");

        ArrayList<String> magnetLinksToTorrents = GetTorrents("The Martian");
        System.out.println("Number of elements: " + magnetLinksToTorrents.size());
        if (magnetLinksToTorrents.size() > 0) {
            for (String link:magnetLinksToTorrents
                 ) {
                System.out.println("WebScraper.main: " + link);
                //OpenMagnetInBrowser(link);
                GetTorrentsFromAllProviders("the mart");
            }
        }
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

    private static ArrayList<String> GetTorrents(String q) {
        q+=" hdrip";
        ArrayList<String> torrentLinkList = new ArrayList<>();
        String query = null;
        try {
            query = URLEncoder.encode(q.toLowerCase(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "https://thepiratebay.org/search/" + query + "/0/99/0";


        String selectorForMagnet = "#searchResult > tbody > tr:nth-child(1) > td:nth-child(2) > a:nth-child(2)";
        Elements torrents;
        try {
            Document document = GetDocument(url);
            torrents = document.select(selectorForMagnet);
            assert torrents != null;
            for (int i = 0; i < torrents.size(); i++) {
                Element element = torrents.get(i);
                String magentLink = element.attr("href");
                torrentLinkList.add(magentLink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return torrentLinkList;
    }

    @Nullable
    public static ArrayList<Torrent> GetTorrentsFromAllProviders(String q) {
        ArrayList<Torrent> torrents = null;
        ArrayList<Provider> providers = Provider.getProvidersList(mContext);

        return torrents;
    }


    private static ArrayList<String> GetTorrentsFromAProvider(String q,String searchURL, String linkSelector) {
        ArrayList<String> torrentLinkList = new ArrayList<>();
        String query = null;
        try {
            query = URLEncoder.encode(q.toLowerCase(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = searchURL.replace("%s",q);
        //String url = "https://thepiratebay.org/search/" + query + "/0/99/0";

        Elements torrents;
        try {
            Document document = GetDocument(url);
            torrents = document.select(linkSelector);
            assert torrents != null;
            for (int i = 0; i < torrents.size(); i++) {
                Element element = torrents.get(i);
                String magentLink = element.attr("href");
                torrentLinkList.add(magentLink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return torrentLinkList;
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