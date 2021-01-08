package no.kristiania.controllers;

import no.kristiania.dao.MemberDao;
import no.kristiania.dao.ProjectTaskDao;
import no.kristiania.database.Member;
import no.kristiania.database.ProjectTask;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateMemberNameController implements HttpController {
    private MemberDao memberDao;

    public UpdateMemberNameController(MemberDao memberDao) {
        this.memberDao = memberDao;
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
        Member member = memberDao.retrieve(targetId);
        member.setName(newName);

        memberDao.updateEntityName(member);

        HttpMessage redirect = new HttpMessage("HTTP/1.1 302 Redirect");
        redirect.setHeader("Location", "http://localhost:8080/projectTasks.html");
        return redirect;
    }
}
