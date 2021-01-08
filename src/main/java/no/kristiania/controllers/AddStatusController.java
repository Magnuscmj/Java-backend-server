package no.kristiania.controllers;

import no.kristiania.database.Status;
import no.kristiania.dao.StatusDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class AddStatusController implements HttpController{

    private final StatusDao statusDao;

    public AddStatusController(StatusDao statusDao) {
        this.statusDao = statusDao;
    }

    @Override
    public void handle(String requestLine, Socket clientSocket) throws IOException, SQLException {
        HttpMessage requestMessage = new HttpMessage(requestLine);
        requestMessage.readHeaders(clientSocket);

        String body = HttpMessage.readBody(clientSocket, requestMessage.getHeader("Content-Length"));

        QueryString requestForm = new QueryString(body);
        Status status = new Status();
        status.setName(requestForm.getParameter("status_name"));
        statusDao.insert(status);


        HttpMessage responseMessage = new HttpMessage("HTTP/1.1 302 Redirect");
        responseMessage.setHeader("Location", "http://localhost:8080/index.html");
        responseMessage.setHeader("Content-Length", String.valueOf(body.length()));
        responseMessage.write(clientSocket);
    }
}
