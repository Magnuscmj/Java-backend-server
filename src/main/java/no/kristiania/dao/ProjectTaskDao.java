package no.kristiania.dao;

import no.kristiania.database.ProjectTask;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class ProjectTaskDao extends AbstractDao<ProjectTask>{

    public ProjectTaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(ProjectTask task) throws SQLException {
        insert(task, "INSERT INTO project_tasks (task_name, statusId) values (?, ?)");
    }

    public ProjectTask retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM project_tasks WHERE id = ?");
    }

    public List<ProjectTask> list() throws SQLException {
        return list("SELECT * FROM project_tasks");
    }
    public List<ProjectTask> list(Integer id) throws SQLException {
        return list(id, "SELECT * FROM project_tasks WHERE id IN (SELECT task_id FROM members_to_tasks WHERE member_id = ?)");
    }

    public void updateEntityName(ProjectTask task) throws SQLException {
        updateEntityName(task, "UPDATE project_tasks SET task_name = ? WHERE id = ?");
    }

    protected void mapEntityToPreparedStatementUpdateName(PreparedStatement statement, ProjectTask entity) throws SQLException{
        statement.setString(1, entity.getName());
        statement.setInt(2, entity.getId());
        statement.executeUpdate();
    }

    protected void mapEntityToPreparedStatement(PreparedStatement statement, ProjectTask entity) throws SQLException{
        statement.setString(1, entity.getName());
        statement.setInt(2, entity.getStatusId());
        statement.executeUpdate();
    }

    @Override
    protected ProjectTask mapRow(ResultSet rs) throws SQLException {
        ProjectTask projectTask = new ProjectTask();
        projectTask.setName(rs.getString("task_name"));
        projectTask.setId(rs.getInt("id"));
        projectTask.setStatusId(rs.getInt("statusId"));
        return projectTask;
    }


    public void update(ProjectTask projectTask) throws SQLException {
        try (Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "UPDATE project_tasks SET statusId = ? WHERE id = ?"

            )){
                statement.setInt(1, projectTask.getStatusId());
                statement.setInt(2, projectTask.getId());
                statement.executeUpdate();

            }
        }
    }
}
