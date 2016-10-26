package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.AleksGirardey.Objects.Core;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class    DatabaseHandler {

    private Logger logger;
    private HikariConfig            config;
    private HikariDataSource        dataSource;
    private Connection              connection;

    @Inject
    public DatabaseHandler(Logger logger) throws IOException, SQLException {

        this.logger = logger;
        File f = new File("WarOfSquirrels/Database.properties");

        if (!f.exists()) {
            if (!f.createNewFile())
                Core.getPlugin().getServer().shutdown();
            FileWriter fw = new FileWriter(f);
            String def =
                    "dataSourceClassName=com.mysql.jdbc.jdbc2.optional.MysqlDataSource\n" +
                            "dataSource.user=root\n" +
                            "dataSource.password=password\n" +
                            "dataSource.databaseName=WarOfSquirrels\n" +
                            "dataSource.portNumber=3306\n" +
                            "dataSource.serverName=localhost";
            fw.write(def);
            fw.close();
            getLogger().info("[Database] Default properties created.");
        }
        getLogger().info("Catching up database properties...");
        config = new HikariConfig("WarOfSquirrels/Database.properties");
        getLogger().info("Creating dataSource...");
        dataSource = new HikariDataSource(config);
        getLogger().info("Everything went good.");
        this.init();
        connection = dataSource.getConnection();
    }

    public void                 init() throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;

        getLogger().info("Initializing sql database...");
        try {
            InputStreamReader   isr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sql/InitTables.sql"));
            Scanner             scanner = new Scanner(isr);
            c = dataSource.getConnection();

            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String          sql = scanner.next();

                statement = c.prepareStatement(sql);
                statement.executeUpdate();
                statement.close();
                statement = null;
            }
            getLogger().info("Database initialization done.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public void     close() throws SQLException {
        this.dataSource.close();
    }

    public Connection       getConnection() throws SQLException {
        return connection;
    }

    public HikariConfig     getConfig() {
        return config;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    private Logger getLogger() {
        return logger;
    }
}