package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {

    private final String startLine;
    private Map<String, String> headers = new HashMap<>();
    private String body;


    public HttpMessage(String startLine){
        this.startLine = startLine;
    }


    public static String readLine(Socket socket) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while((c = socket.getInputStream().read()) != -1){
            if(c == '\r'){
                socket.getInputStream().read();
                break;
            }
            line.append((char) c);
        }
        return line.toString();
    }

    public static String readBody(Socket socket, String contentLengthHeader) throws IOException {
        int contentLength = Integer.parseInt(contentLengthHeader);
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) socket.getInputStream().read());
        }
        return body.toString();
    }

    public void setHeader(String name, String value){
        headers.put(name, value);
        headers.put("Connection", "close");
    }

    public void write(Socket socket) throws IOException {
        writeLine(socket, startLine);
        for(Map.Entry<String, String> header : headers.entrySet()){
            writeLine(socket, header.getKey()+ ": " + header.getValue());
        }
        writeLine(socket, "");
        if(body != null){
            socket.getOutputStream().write(body.getBytes());
        }


    }

    private void writeLine(Socket socket, String startLine) throws IOException {
        socket.getOutputStream().write((startLine + "\r\n").getBytes());
    }

    public String getStartLine() {
        return startLine;
    }

    public void readHeaders(Socket socket) throws IOException {
        String headerLine;
        while(!(headerLine = HttpMessage.readLine(socket)).isEmpty()){
            int colonPos = headerLine.indexOf(':');
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();

            setHeader(headerName, headerValue);
        }
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static HttpMessage read(Socket socket) throws IOException {
        HttpMessage message = new HttpMessage(readLine(socket));
        message.readHeaders(socket);
        return message;
    }

    public String readBody(Socket socket) throws IOException {
        int contentLength = Integer.parseInt(getHeader("Content-Length"));
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) socket.getInputStream().read());
        }
        return body.toString();
    }

    public String getBody() {
        return body;
    }
}


