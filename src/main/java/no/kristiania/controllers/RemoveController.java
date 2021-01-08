package no.kristiania.controllers;

import no.kristiania.dao.MemberDao;
import no.kristiania.dao.MemberToTaskDao;
import no.kristiania.dao.ProjectTaskDao;
import no.kristiania.dao.StatusDao;
import no.kristiania.database.MemberToTask;
import no.kristiania.database.ProjectTask;
import no.kristiania.database.Status;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class RemoveController implements HttpController {

    private final MemberToTaskDao memberToTaskDao;

    public RemoveController(MemberToTaskDao memberToTaskDao) {
        this.memberToTaskDao = memberToTaskDao;
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
        Integer memberId = Integer.valueOf(requestParameter.getParameter("memberId"));
        Integer taskId = Integer.valueOf(requestParameter.getParameter("taskId"));
        MemberToTask memberToTask = new MemberToTask();
        memberToTask.setMemberId(memberId);
        memberToTask.setTaskId(taskId);
        memberToTaskDao.delete(memberToTask);


        HttpMessage redirect = new HttpMessage("HTTP/1.1 302 Redirect");
        redirect.setHeader("Location", "http://localhost:8080/index.html");
        return redirect;
    }
}
