package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.dao.MemberDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {

    private JdbcDataSource dataSource;
    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        server = new HttpServer(0, dataSource);
    }

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo");
        assertEquals(200, client.getResponseCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=404");
        assertEquals(404, client.getResponseCode());
    }

    @Test
    void shouldReturnHttpHeaders() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnFileContent() throws IOException {
        File documentRoot = new File("target/test-classes/public");
        documentRoot.mkdirs();
        String fileContent = "Hello " + new Date();
        Files.writeString(new File(documentRoot, "index.html").toPath(), fileContent);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/index.html");
        assertEquals(fileContent, client.getResponseBody());
    }

    @Test
    void shouldReturn404onMissingFile() throws IOException {
        File documentRoot = new File("target/test-classes");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/missingFile");
        assertEquals(404, client.getResponseCode());
    }

    @Test
    void shouldReturn404onFileOutsideDocumentRoot() throws IOException {
        File documentRoot = new File("target/test-classes");
        Files.writeString(new File(documentRoot, "secret.txt").toPath(), "Super secret file");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/../secret.txt");
        assertEquals(404, client.getResponseCode());
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        File documentRoot = new File("target/test-classes/public");
        documentRoot.mkdirs();
        Files.writeString(new File(documentRoot, "plain.txt").toPath(), "Plain text");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/plain.txt");
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldPostMember() throws IOException {
        QueryString member = new QueryString("");
        member.addParameter("full_name", "Marius");
        member.addParameter("email_address", "austheim.marius@gmail.com");
        new HttpClient("localhost", server.getPort(), "/members", "POST", member);
        assertEquals(List.of("Marius"), server.getMemberNames());
    }

    @Test
    void shouldDisplayExistingMember() throws IOException, SQLException {
        MemberDao memberDao = new MemberDao(dataSource);
        Member member = new Member();
        member.setName("Petter");
        memberDao.insert(member);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/projectMembers");
        assertThat(client.getResponseBody().contains("<li>Petter</li>"));
    }

    @Test
    void shouldPostNewTask() throws IOException {
        QueryString task = new QueryString("");
        task.addParameter("task_name", "Male");
        task.addParameter("statusId", "1");
        HttpClient postClient = new HttpClient("localhost", server.getPort(), "/newProjectTasks", "POST", task);
        assertEquals(302, postClient.getResponseCode());

        HttpClient getClient = new HttpClient("localhost", server.getPort(), "/projectTasks");
        assertThat(getClient.getResponseBody().contains("<li>Male</li>"));


    }

}