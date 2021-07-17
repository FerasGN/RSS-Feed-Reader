package com.feeedify.rest.service.favicon;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.OkHttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import static com.feeedify.rest.service.favicon.Utils.*;


/**
 * Load favicons for a {@link Source}. If the source's HTML is not specified, it will be loaded.
 * If the html contains favicon links, the domain favicons will be omitted.
 * Can be configured to run network operations on a different thread by passing in different {@link Executor}s.
 */
public class FaviconLoader {

  private static final String ICON_LINK_SELECTOR = "link[rel~=^(.*\\s|)icon(|\\s.*)$]";

  /**
   * Passed the results of {@code getFavicons}.
   */
  public static interface FaviconCallback {
    void onFaviconsLoaded(Set<URL> favicons);
  }

  private HttpClient client;
  private Executor worker;
  private Executor responder;

  public FaviconLoader() {
    client = new OkHttpHttpClient(new OkHttpClient());
    worker = SynchronousExecutor.getInstance();
    responder = SynchronousExecutor.getInstance();
  }

  public FaviconLoader setClient(HttpClient client) {
    this.client = checkNotNull(client, "client");
    return this;
  }

  public FaviconLoader setWorker(Executor worker) {
    this.worker = checkNotNull(worker, "worker");
    return this;
  }

  public FaviconLoader setResponder(Executor responder) {
    this.responder = checkNotNull(responder, "responder");
    return this;
  }

  public void getFavicons(String url, FaviconCallback callback) {
    Source source = Source.fromUrl(url).build();
    getFavicons(source, callback);
  }

  public void getFavicons(String url, String body, FaviconCallback callback) {
    Source source = Source.fromUrl(url).withBody(body).build();
    getFavicons(source, callback);
  }

  public void getFavicons(URL url, FaviconCallback callback) {
    Source source = Source.fromUrl(url).build();
    getFavicons(source, callback);
  }

  public void getFavicons(URL url, String body, FaviconCallback callback) {
    Source source = Source.fromUrl(url).withBody(body).build();
    getFavicons(source, callback);
  }

  void getFavicons(Source source, FaviconCallback callback) {
    worker.execute(new Worker(client, responder, source, callback));
  }

  private static class Worker implements Runnable {
    private final HttpClient client;
    private final Executor responder;
    private final Source source;
    private final FaviconCallback callback;

    public Worker(HttpClient client, Executor responder, Source source, FaviconCallback callback) {
      this.client = client;
      this.responder = responder;
      this.source = source;
      this.callback = callback;
    }

    @Override
    public void run() {
      final Set<URL> favicons = new HashSet<>();
      final String html = getOrLoadBody(source);

      getFaviconsFromHtml(html, favicons);

      if (favicons.isEmpty()) {
        getDomainFavicons(source, favicons);
      }

      responder.execute(new Runnable() {
        @Override
        public void run() {
          callback.onFaviconsLoaded(favicons);
        }
      });
    }

    /**
     * Extract all the elements from an HTML document that define a link to a favicon resource.
     * Ignores all parsing-related exceptions.
     */
    private void getFaviconsFromHtml(final String html, final Set<URL> favicons) {
      // Passing the base URI allows JSoup to resolve URLs for us automatically.
      final String baseUri = source.getUrl().toString();
      final Document document = Jsoup.parse(html, baseUri);
      final Elements faviconLinks = document.head().select(ICON_LINK_SELECTOR);

      for (Element elem : faviconLinks) {
        final String href = elem.absUrl("href");

        try {
          favicons.add(new URL(href));
        } catch (MalformedURLException e) {
          // Ignore errors parsing individual link elements.
        }
      }
    }

    /**
     * Finds all valid domain favicons for a URL.
     */
    private void getDomainFavicons(final Source source, final Set<URL> favicons) {
      for (URL favicon : source.getDomainFavicons()) {
        if (client.doesResourceExist(favicon)) {
          favicons.add(favicon);
        }
      }
    }

    /**
     * Returns the {@link Source}'s HTML if specified, else gets the body from the network.
     */
    private String getOrLoadBody(Source source) {
      String html = source.getBody();
      if (html == null) {
        html = client.tryLoadBody(source.getUrl());
      }
      return html;
    }
  }
}
