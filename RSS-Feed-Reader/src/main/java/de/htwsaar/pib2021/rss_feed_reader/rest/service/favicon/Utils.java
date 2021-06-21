package de.htwsaar.pib2021.rss_feed_reader.rest.service.favicon;

import java.net.MalformedURLException;
import java.net.URL;

final class Utils {
    public static <T> T checkNotNull(T obj, String description) {
        if (obj == null) {
            throw new IllegalArgumentException(description + " cannot be null");
        }
        return obj;
    }

    public static URL parseUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Utils() {
    }
}