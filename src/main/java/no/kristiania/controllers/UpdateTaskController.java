package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.dao.ProjectTaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateTaskController implements HttpController {
    private ProjectTaskDao projectTaskDao;

    public UpdateTaskController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
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
        Integer taskId = Integer.valueOf(requestParameter.getParameter("taskId"));
        Integer statusId = Integer.valueOf(requestParameter.getParameter("status"));
        ProjectTask task = projectTaskDao.retrieve(taskId);
        task.setStatusId(statusId);

        projectTaskDao.update(task);

        HttpMessage redirect = new HttpMessage("HTTP/1.1 302 Redirect");
        redirect.setHeader("Location", "http://localhost:8080/projectTasks.html");
        return redirect;
    }
}
