package com.baliyaan.android.imdbtor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 12/25/2016.
 */

public class TorrentProvider {
    public String title;
    public String searchURL;
    public String magnetsSelector;
    public String titlesSelector;
    public String timestampsSelector;
    public String sizesSelector;
    public String seedsSelector;
    public String leechesSelector;
    public String URLsSelector;

    public ArrayList<Torrent> GetTorrents(String q) {

        // Initialize Output List
        ArrayList<Torrent> torrentsList = new ArrayList<>();

        // Reformat Query String
        String query = null;
        try {
            query = URLEncoder.encode(q.toLowerCase(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Prepare Search URL
        //String url = "https://thepiratebay.org/search/" + query + "/0/99/0";
        String url = searchURL.replace("%s", query);

        // Fill torrents fields from the provider
        //String selectorForMagnet = "#searchResult > tbody > tr:nth-child(1) > td:nth-child(2) > a:nth-child(2)";
        try {

            // Retrieve torrent list page
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();

            // Retrieve titles list
            Elements titlesList;
            titlesList = document.select(titlesSelector);
            // Retrieve fields list
            Elements magnetsList;
            magnetsList = document.select(magnetsSelector);
            // Retrieve timestamps
            Elements timestampsList;
            timestampsList = document.select(timestampsSelector);
            // Retrieve sizes
            Elements sizesList;
            sizesList = document.select(sizesSelector);
            // Retrieve seeds
            Elements seedsList;
            seedsList = document.select(seedsSelector);
            // Retrieve leeches
            Elements leechesList;
            leechesList = document.select(leechesSelector);
            // Retrieve URLs
            Elements URLsList;
            URLsList = document.select(URLsSelector);

            // Map all the fields
            for (int i = 0; i < magnetsList.size(); i++) {
                Torrent torrent = new Torrent();
                torrent.magnetLink = magnetsList.get(i).attr("href");
                torrent.title = titlesList.get(i).text();
                //torrent.timeAdded = timestampsList.get(i).text();
                //torrent.size = sizesList.get(i).text();
                torrent.seeds = seedsList.get(i).text();
                torrent.leeches = leechesList.get(i).text();
                torrent.url = URLsList.get(i).attr("abs:href");
                torrent.provider = title;
                torrentsList.add(torrent);
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }

        // Return output list
        return torrentsList;
    }
}

