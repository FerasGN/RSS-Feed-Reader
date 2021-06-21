package de.htwsaar.pib2021.rss_feed_reader.rest.service.favicon;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/** An {@link HttpClient} backed by OkHttp. */
class OkHttpHttpClient implements HttpClient {
    private final OkHttpClient client;

    public OkHttpHttpClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public String tryLoadBody(URL url) {
        String res = null;
        try {
            final Request req = new Request.Builder().url(url).get().build();
            final Response resp = client.newCall(req).execute();
            final int code = resp.code();
            if (code == 200) {
                final ResponseBody body = resp.body();
                res = body.string();
                body.close(); // even this doesn't work!
            }
        } catch (final Throwable th) {
            System.out.println(th.getMessage());
        }

        return res;
    }

    @Override
    public boolean doesResourceExist(URL url) {
        final Request request = new Request.Builder().url(url).head().build();

        try {
            return client.newCall(request).execute().isSuccessful();
        } catch (IOException e) {
            throw new RuntimeException("Error checking for resource at " + url, e);
        }
    }
}
