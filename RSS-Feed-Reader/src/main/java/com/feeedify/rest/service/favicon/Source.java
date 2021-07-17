package com.feeedify.rest.service.favicon;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static com.feeedify.rest.service.favicon.Utils.*;

/**
 * A thin wrapper around a {@link java.net.URL} for a webpage.
 */
class Source {
    private static final String[] DOMAIN_FAVICON_PATHS = new String[] { "/favicon.ico", "/favicon.png",
            "/favicon.gif" };

    public static Builder fromUrl(String url) {
        return new Builder(parseUrl(url));
    }

    public static Builder fromUrl(URL url) {
        return new Builder(url);
    }

    public static class Builder {
        private final URL url;
        private String body;

        private Builder(URL url) {
            this.url = checkNotNull(url, "url");
        }

        public Builder withBody(String html) {
            this.body = html;
            return this;
        }

        public Source build() {
            return new Source(url, body);
        }
    }

    private final URL url;
    private final String body;

    private Source(URL url, String body) {
        this.url = url;
        this.body = body;
    }

    public URL getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    /**
     * Returns a list of URLs created by appending common favicon filenames to the
     * root of the page URL. E.g. {@code http://example.com/index.html} will give
     * {@code http://example.com/favicon.ext} where {@code ext} is one of:
     * <ul>
     * <li>ico</li>
     * <li>png</li>
     * <li>gif</li>
     * </ul>
     */
    public Set<URL> getDomainFavicons() {
        final Set<URL> favicons = new HashSet<>();
        URI pageUri;

        try {
            pageUri = url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            for (String path : DOMAIN_FAVICON_PATHS) {
                favicons.add(pageUri.resolve(path).toURL());
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        return favicons;
    }
}