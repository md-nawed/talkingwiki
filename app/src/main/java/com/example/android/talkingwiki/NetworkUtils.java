package com.example.android.talkingwiki;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by PC on 20-08-2017.
 */

public class NetworkUtils {

    final static String WIKI_BASE_URL = "https://www.mediawiki.org/w/api.php";

    final static String PARAM_QUERY = "q";


    public static URL buildUrl(String wikiSearchQuery) {
        Uri builtUri = Uri.parse(WIKI_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, wikiSearchQuery)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
