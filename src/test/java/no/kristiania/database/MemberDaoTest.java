package no.kristiania.database;

import no.kristiania.controllers.ProjectMemberOptionsController;
import no.kristiania.dao.MemberDao;
import no.kristiania.dao.ProjectTaskDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberDaoTest {

    private MemberDao memberDao;
    private static final Random random = new Random();
    private ProjectTaskDao taskDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        memberDao = new MemberDao(dataSource);
        taskDao = new ProjectTaskDao(dataSource);
    }

    @Test
    void shouldListInsertedMembers() throws SQLException {
        Member member1 = exampleMember();
        Member member2 = exampleMember();
        memberDao.insert(member1);
        memberDao.insert(member2);
        assertThat(memberDao.list())
                .extracting(Member :: getName)
                .contains(member1.getName(), member2.getName());
    }

    @Test
    void shouldRetrieveAllMemberProperties() throws SQLException {
        memberDao.insert(exampleMember());
        memberDao.insert(exampleMember());
        Member member = exampleMember();
        memberDao.insert(member);
        assertThat(member).hasNoNullFieldsOrPropertiesExcept("taskId", "statusId");
        assertThat(memberDao.retrieve(member.getId()))
                .usingRecursiveComparison()
                .isEqualTo(member);
    }

    @Test
    void shouldReturnMembersAsOptions() throws SQLException {
        ProjectMemberOptionsController controller = new ProjectMemberOptionsController(memberDao);
        Member member = MemberDaoTest.exampleMember();
        memberDao.insert(member);

        assertThat(controller.getBody())
                .contains("<option value=" + member.getId() + ">" + member.getName() + "</option>");
    }

    /*@Test
    void shouldUpdateExistingMemberWithNewTask() throws IOException, SQLException {
        UpdateMemberController controller = new UpdateMemberController(memberDao);

        Member member = exampleMember();
        memberDao.insert(member);

        ProjectTask task = ProjectTaskDaoTest.exampleTask();
        task.setStatusId(1);
        taskDao.insert(task);


        String body = "memberId="+ member.getId() + "&taskId=" + task.getId();
        controller.handle(new QueryString(body));

        assertThat(memberDao.retrieve(member.getId()).getTaskId())
                .isEqualTo(task.getId());


    }*/

    public static Member exampleMember() {
        Member member = new Member();
        member.setName(exampleMemberName());
        member.setEmail(exampleMemberEmail());
        return member;

    }

    private static String exampleMemberName() {
        String[] names = {"Petter", "Marius", "Thomine", "Oda"};
        return names[random.nextInt(names.length)];
    }

    private static String exampleMemberEmail() {
        String[] emails = {"petter@gmail.com", "Marius@gmail.com", "Thomine@gmail.com", "Oda@gmail.com"};
        return emails[random.nextInt(emails.length)];
    }


}