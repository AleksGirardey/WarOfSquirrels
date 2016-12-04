package fr.AleksGirardey.Objects.Database;

import fr.AleksGirardey.Objects.Core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Statement {
    private Connection              _connection = null;
    private PreparedStatement       _statement = null;
    private ResultSet               _result = null;
    private ResultSet               _keys = null;

    public Statement() {
        try {
            _connection = Core.getDatabaseHandler().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Statement(String sql) {
        try {
            _connection = Core.getDatabaseHandler().getConnection();
            NewQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void NewQuery(String sql) throws SQLException {
        _statement = _connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
    }

    public PreparedStatement    getStatement() { return _statement; }
    public ResultSet            getResult() { return _result; }
    public ResultSet            getKeys() { return _keys; }

    public void         Update() throws SQLException {
        _statement.executeUpdate();
        _keys = _statement.getGeneratedKeys();
    }

    public ResultSet    Execute() throws SQLException {
        _result = _statement.executeQuery();
        return _result;
    }

    public void Close() throws SQLException {
        if (_statement != null)
            _statement.close();
        if (_result != null)
            _result.close();
    }
}
