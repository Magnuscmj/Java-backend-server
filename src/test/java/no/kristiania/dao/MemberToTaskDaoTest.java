package no.kristiania.dao;

import no.kristiania.controllers.AddTaskToMemberController;
import no.kristiania.controllers.RemoveController;
import no.kristiania.database.Member;
import no.kristiania.database.MemberToTask;
import no.kristiania.database.ProjectTask;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberToTaskDaoTest {

    private MemberToTaskDao memberToTaskDao;
    private ProjectTaskDao projectTaskDao;
    private MemberDao memberDao;
    private static final Random random = new Random();


    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        memberToTaskDao = new MemberToTaskDao(dataSource);
        memberDao = new MemberDao(dataSource);
        projectTaskDao = new ProjectTaskDao(dataSource);
    }

    @Test
    void shouldListMembersWithTasks() throws SQLException {
        AddTaskToMemberController controller = new AddTaskToMemberController(memberToTaskDao);
        HttpMessage response;


        ProjectTask task1 = exampleTask();
        task1.setStatusId(1);
        ProjectTask task2 = exampleTask();
        task2.setStatusId(1);

        projectTaskDao.insert(task1);
        projectTaskDao.insert(task2);

        Member member1 = exampleMember();
        Member member2 = exampleMember();

        member1.setTaskId(task1.getId());
        member2.setTaskId(task2.getId());

        memberDao.insert(member1);
        memberDao.insert(member2);


        String body = "memberId=" + member1.getId() + "&taskId=" + task1.getId();
        controller.handle(new QueryString(body));

        body = "memberId=" + member1.getId() + "&taskId=" + task2.getId();
        controller.handle(new QueryString(body));

        body = "memberId=" + member2.getId() + "&taskId=" + task2.getId();

        response = controller.handle(new QueryString(body));


        assertThat(memberDao.list())
                .extracting(Member :: getName)
                .contains(member1.getName(), member2.getName());
        assertThat(projectTaskDao.list(member1.getId()))
                .extracting(ProjectTask :: getName)
                .contains(task1.getName(), task2.getName());
        assertThat(response.getStartLine())
                .isEqualTo("HTTP/1.1 302 Redirect");
        assertThat(response.getHeader("Location"))
                .isEqualTo("http://localhost:8080/index.html");
    }


    public static Member exampleMember() {
        Member member = new Member();
        member.setName(exampleMemberName());
        member.setEmail(exampleMemberEmail());
        return member;
    }

    public static ProjectTask exampleTask() {
        ProjectTask task = new ProjectTask();
        task.setName(exampleMemberTask());
        return task;
    }

    private static String exampleMemberName() {
        String[] names = {"Petter", "Marius", "Thomine", "Oda"};
        return names[random.nextInt(names.length)];
    }

    private static String exampleMemberEmail() {
        String[] emails = {"test1@mail.com", "test2@mail.com", "test3@mail.com", "test4@mail.com"};
        return emails[random.nextInt(emails.length)];
    }

    private static String exampleMemberTask() {
        String[] tasks = {"Male", "Rydde", "Suge", "Vaske"};
        return tasks[random.nextInt(tasks.length)];
    }
}