package no.kristiania.dao;

import no.kristiania.database.MemberToTask;
import no.kristiania.database.ProjectTask;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MemberToTaskDao extends AbstractDao<MemberToTask> {

    public MemberToTaskDao(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    protected void mapEntityToPreparedStatementUpdateName(PreparedStatement statement, MemberToTask entity) throws SQLException {

    }

    public void insert(MemberToTask memberToTask) throws SQLException {
        insert(memberToTask, "INSERT INTO members_to_tasks (member_id, task_id) values (?, ?)");
    }

    public MemberToTask retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM members_to_tasks WHERE task_id = ?");
    }

    public List<MemberToTask> list() throws SQLException {
        return list("SELECT * FROM members_to_tasks");
    }


    public void delete(MemberToTask memberToTask) throws SQLException {
        delete(memberToTask, "DELETE FROM members_to_tasks WHERE member_id = ? AND task_id = ?");
    }
    @Override
    protected void mapEntityToPreparedStatement(PreparedStatement statement, MemberToTask entity) throws SQLException {
        statement.setInt(1, entity.getMemberId());
        statement.setInt(2, entity.getTaskId());
        statement.executeUpdate();
    }

    @Override
    protected MemberToTask mapRow(ResultSet rs) throws SQLException {
        MemberToTask memberToTask = new MemberToTask();
        memberToTask.setId(rs.getInt("id"));
        memberToTask.setMemberId(rs.getInt("member_id"));
        memberToTask.setTaskId(rs.getInt("task_id"));
        return memberToTask;
    }
}
