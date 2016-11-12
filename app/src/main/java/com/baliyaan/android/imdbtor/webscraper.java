package com.baliyaan.android.imdbtor;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    public static ArrayList<String> Select(Document iDocument,String iSelector)
    {
        ArrayList<String> selection = new ArrayList<String>();
        Elements elements = iDocument.select(iSelector);
        assert elements!=null;
        for (int i = 0; i < elements.size() ; i++) {
            Element element = elements.get(i);
            String elemText = element.text();
            selection.add(elemText);
        }
        return selection;
    }

    public static void main(String[] args) {
        try {
            //Document document = GetDocument("https://www.imdb.com/user/ur27429396/watchlist?ref_=wt_nv_wl_all_0");
            Document document = Jsoup.parse(new File("D:/Repo/IMDBTor/imdblist.html"),"ISO-8859-1");
            ArrayList<String> movies = Select(document,"#main > div > span > #center-1-react > div > div:nth-child(3) > div.lister-list.mode-detail > #page-1 > div > div > div.lister-item-content > h3 > a");
            //ArrayList<String> movies = Select(document,"#main > div > span > #center-1-react > div:nth-child(3)");
            System.out.println("Number of elements: " + movies.size());
            if(movies.size()>0) {
                System.out.println("webscraper.main: " + movies.get(0));
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}