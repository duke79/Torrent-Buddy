package com.baliyaan.android.imdbtor;

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

public class webscraper {
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

        ArrayList<String> torrents = GetTorrents("The Martian");
        System.out.println("Number of elements: " + torrents.size());
        if (torrents.size() > 0) {
            System.out.println("webscraper.main: " + torrents.get(0));
        }
    }

    private static ArrayList<String> GetTorrents(String q) {
        String query = null;
        try {
            query = URLEncoder.encode(q.toLowerCase(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "https://thepiratebay.org/search/" + query + "/0/99/0";
        String selector = "#searchResult > tbody > tr:nth-child(1) > td:nth-child(2) > a:nth-child(2)";
        ArrayList<String> torrents = new ArrayList<>();
        try {
            Document document = GetDocument(url);
            torrents = Select(document, selector);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return torrents;
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