package no.kristiania.controllers;

import no.kristiania.dao.ProjectTaskDao;
import no.kristiania.database.ProjectTask;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateTaskNameController implements HttpController{

        private ProjectTaskDao projectTaskDao;

        public UpdateTaskNameController(ProjectTaskDao projectTaskDao) {
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
            Integer targetId = Integer.valueOf(requestParameter.getParameter("targetId"));
            String newName = String.valueOf(requestParameter.getParameter("newName"));
            ProjectTask task = projectTaskDao.retrieve(targetId);
            task.setName(newName);

            projectTaskDao.updateEntityName(task);

            HttpMessage redirect = new HttpMessage("HTTP/1.1 302 Redirect");
            redirect.setHeader("Location", "http://localhost:8080/projectTasks.html");
            return redirect;
        }
}
