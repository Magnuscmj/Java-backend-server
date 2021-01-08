package no.kristiania.http;


import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private String responseBody;
    private HttpMessage responseMessage;

    public HttpClient(String hostname, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(hostname, port);

        HttpMessage requestMessage = new HttpMessage("GET " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.write(socket);

        responseMessage = HttpMessage.read(socket);

        responseBody = responseMessage.readBody(socket);
    }

    public HttpClient(String hostName, int port, String requestTarget, String method, QueryString form) throws IOException {
        Socket socket = new Socket(hostName, port);

        String requestBody = form.getQueryString();

        HttpMessage requestMessage = new HttpMessage(method + " " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostName);
        requestMessage.setHeader("Content-Length", String.valueOf(requestBody.length()));
        requestMessage.write(socket);
        socket.getOutputStream().write(requestBody.getBytes());

        responseMessage = HttpMessage.read(socket);

    }

    public static void main(String[] args) throws IOException {
        String hostname = "urlecho.appspot.com";
        int port = 80;
        String requestTarget = "/echo?status=200&body=Hello%20world!";
        new HttpClient(hostname, port, requestTarget);
    }


    public int getResponseCode() {
        String[] responseLineParts = responseMessage.getStartLine().split(" ");
        int responseCode = Integer.parseInt(responseLineParts[1]);
        return responseCode;
    }

    public String getResponseHeader(String headerName) {
        return responseMessage.getHeader(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}
