package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryStringTest {
    @Test
    void shouldRetrieveQueryParameter() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("200", queryString.getParameter("status"));

    }

    @Test
    void ShouldRetrieveOtherQueryParameter() {
        QueryString queryString = new QueryString("status=404");
        assertEquals("404", queryString.getParameter("status"));
    }

    @Test
    void ShouldRetrieveParameterByName() {
        QueryString queryString = new QueryString("text=Hello");
        assertEquals(null, queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("text"));
    }

    @Test
    void ShouldHandleMultipleParameters() {
        QueryString queryString = new QueryString("text=Hello&status=200");
        assertEquals("200", queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("text"));
    }
    @Test
    void ShouldSerializeQueryString() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("status=200", queryString.getQueryString());
        queryString.addParameter("body", "Hello");
        assertEquals("status=200&body=Hello", queryString.getQueryString());
    }

}
