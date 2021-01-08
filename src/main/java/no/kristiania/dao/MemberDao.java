package no.kristiania.dao;

import no.kristiania.database.Member;
import no.kristiania.database.ProjectTask;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class MemberDao extends AbstractDao<Member> {

    public MemberDao(DataSource dataSource) {
        super(dataSource);

    }

    public void insert(Member member) throws SQLException {
        insert(member, "INSERT INTO members (member_name, email) values (?, ?)");
    }


    public Member retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM members WHERE id = ?");
    }

    public List<Member> list() throws SQLException {
        return list("SELECT * FROM members");
    }

    public List<Member> list(Integer id) throws SQLException {
        return list(id, "SELECT * FROM members WHERE id IN (SELECT member_id FROM members_to_tasks WHERE task_id = ?)");
    }

    public void updateEntityName(Member member) throws SQLException {
        updateEntityName(member, "UPDATE members SET member_name = ? WHERE id = ?");
    }

    protected void mapEntityToPreparedStatementUpdateName(PreparedStatement statement, Member entity) throws SQLException{
        statement.setString(1, entity.getName());
        statement.setInt(2, entity.getId());
        statement.executeUpdate();
    }

    protected void mapEntityToPreparedStatement(PreparedStatement statement, Member entity) throws SQLException{
        statement.setString(1, entity.getName());
        statement.setString(2, entity.getEmail());
        statement.executeUpdate();
    }


    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setTaskId((Integer) rs.getObject("taskId"));
        member.setName(rs.getString("member_name"));
        member.setEmail(rs.getString("email"));
        return member;
    }

    public void update(Member member) throws SQLException {
        try (Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "UPDATE members SET taskId = ? WHERE id = ?"

            )){
                statement.setInt(1, member.getTaskId());
                statement.setInt(2, member.getId());
                statement.executeUpdate();

            }
        }
    }
}
