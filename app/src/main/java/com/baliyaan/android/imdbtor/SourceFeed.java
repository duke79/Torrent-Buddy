package com.baliyaan.android.imdbtor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SourceFeed{

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

    public static ArrayList<String> GetIMDBWatchlist() {
        String url = "https://www.imdb.com/user/ur27429396/watchlist";
        Document document = null;
        try {
            document =
                    GetDocument(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String html = document.toString();
        String titleSelector = "#center-1-react > div > div:nth-child(3) > div.lister-list.mode-detail > div > div > div.lister-item-content > h3 > a";
        ArrayList<String> movies = Select(document,titleSelector);
        return movies;
    }
}
