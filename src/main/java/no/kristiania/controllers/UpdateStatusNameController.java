package no.kristiania.controllers;

import no.kristiania.dao.MemberDao;
import no.kristiania.dao.StatusDao;
import no.kristiania.database.Member;
import no.kristiania.database.Status;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateStatusNameController implements HttpController {

    private StatusDao statusDao;

    public UpdateStatusNameController(StatusDao statusDao) {
        this.statusDao = statusDao;
    }

    @Override
    public void handle(String requestLine, Socket clientSocket) throws IOException, SQLException {
        HttpMessage requestMessage = new HttpMessage(requestLine);
        requestMessage.readHeaders(clientSocket);

        String body = HttpMessage.readBody(clientSocket, requestMessage.getHeader("Content-Length"));

        QueryString requestForm = new QueryString(body);
        HttpMessage response = handle(requestForm);
        response.write(clientSocket);
    }

    public HttpMessage handle(QueryString requestParameter) throws SQLException {
        Integer targetId = Integer.valueOf(requestParameter.getParameter("targetId"));
        String newName = String.valueOf(requestParameter.getParameter("newName"));
        Status status = statusDao.retrieve(targetId);
        status.setName(newName);

        statusDao.updateEntityName(status);

        HttpMessage redirect = new HttpMessage("HTTP/1.1 302 Redirect");
        redirect.setHeader("Location", "http://localhost:8080/projectTasks.html");
        return redirect;
    }
}
