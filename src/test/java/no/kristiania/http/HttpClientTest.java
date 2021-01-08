package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {
    @Test
    void shouldReadSuccessfulResponseCode() throws IOException {
        HttpClient httpClient = makeEchoRequest("/echo?status=200");
        assertEquals(200, httpClient.getResponseCode());
    }

    private HttpClient makeEchoRequest(String requestTarget) throws IOException {
        return new HttpClient("urlecho.appspot.com", 80, requestTarget);
    }

    @Test
    void shouldReadFailureResponseCode() throws IOException {
        HttpClient client = makeEchoRequest("/echo?status=401");
        assertEquals(401, client.getResponseCode());
    }

    @Test
    void shouldReadHeaders() throws IOException {
        HttpClient client = makeEchoRequest("/echo?body=Kristiania");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReadBody() throws IOException {
        HttpClient client = makeEchoRequest("/echo?body=Kristiania");
        assertEquals("Kristiania", client.getResponseBody());
    }
}