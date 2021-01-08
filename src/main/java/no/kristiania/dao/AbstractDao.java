package no.kristiania.dao;

import no.kristiania.database.IdEntity;
import no.kristiania.database.MemberToTask;
import no.kristiania.database.ProjectTask;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T extends IdEntity> {
    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected T retrieve(Integer id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    } else {
                        return null;
                    }

                }
            }
        }
    }

    public List<T> list(String sql) throws SQLException {
        List<T>  list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapRow(rs));
                    }
                }
            }
        }
        return list;
    }

    public List<T> list(Integer id,String sql) throws SQLException {
        List<T>  list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapRow(rs));
                    }
                }
            }
        }
        return list;
    }

    public void delete(T entity, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    sql
            )){
                mapEntityToPreparedStatement(statement, entity);
            }
        }
    }

    public void insert(T entity, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )){
                mapEntityToPreparedStatement(statement, entity);

                fillGeneratedKeys(entity, statement);
            }
        }
    }

    public void updateEntityName(T entity, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    sql

            )){
                mapEntityToPreparedStatementUpdateName(statement, entity);

            }
        }
    }

    protected abstract void mapEntityToPreparedStatementUpdateName(PreparedStatement statement, T entity) throws SQLException;


    protected abstract void mapEntityToPreparedStatement(PreparedStatement statement, T entity) throws SQLException;

    public static void fillGeneratedKeys(IdEntity entity, PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            generatedKeys.next();
            entity.setId(generatedKeys.getInt("id"));
        }
    }

    protected abstract T mapRow(ResultSet rs) throws SQLException;


}
