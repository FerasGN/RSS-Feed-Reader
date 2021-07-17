package com.feeedify.rest.service.favicon;

import java.net.URL;

/**
 * Simple HTTP client.
 */
public interface HttpClient {
    String tryLoadBody(URL url);

    boolean doesResourceExist(URL url);
}
