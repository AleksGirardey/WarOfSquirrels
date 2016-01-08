package fr.AleksGirardey.Objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Statement {
    private Connection      _connection = null;
    private PreparedStatement       _statement = null;
    private ResultSet               _result = null;

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
        _statement = _connection.prepareStatement(sql);
    }

    public PreparedStatement    getStatement() { return _statement; }
    public ResultSet            getResult() { return _result; }

    public void         Update() throws SQLException {
        _statement.executeUpdate();
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
