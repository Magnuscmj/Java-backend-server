package no.kristiania.controllers;

import no.kristiania.dao.MemberDao;
import no.kristiania.dao.StatusDao;
import no.kristiania.database.Member;
import no.kristiania.database.ProjectTask;
import no.kristiania.dao.ProjectTaskDao;
import no.kristiania.database.Status;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskGetController implements HttpController {

    private ProjectTaskDao projectTaskDao;
    private MemberDao memberDao;
    private StatusDao statusDao;

    public ProjectTaskGetController(ProjectTaskDao projectTaskDao, MemberDao memberDao, StatusDao statusDao) {
        this.projectTaskDao = projectTaskDao;
        this.memberDao = memberDao;
        this.statusDao = statusDao;
    }

    @Override
    public void handle(String requestLine, Socket clientSocket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder("<ul>");
        String statusName = "";
        for (ProjectTask task : projectTaskDao.list()) {
            for(Status status : statusDao.list()){
                if(task.getStatusId().equals(status.getId())){
                    statusName = status.getName();
                }
            }
            body.append("<li>").append(task.getName() + " (Status: <strong>" + statusName + "</strong>) - <i>Members on task:</i>" ).append("<ul>");
            for(Member member : memberDao.list(task.getId())){
                    body.append("<li>").append(member.getName()).append("</li>");
            }

            body.append("</ul>").append("</li>");
        }

        body.append("</ul>");


            HttpMessage responseMessage = new HttpMessage("HTTP/1.1 200 OK");
            responseMessage.setHeader("Connection", "close");
            responseMessage.setHeader("Content-Type", "text/html");
            responseMessage.setHeader("Content-Length", String.valueOf(body.length()));
            responseMessage.setBody(body.toString());
            responseMessage.write(clientSocket);


    }
}
