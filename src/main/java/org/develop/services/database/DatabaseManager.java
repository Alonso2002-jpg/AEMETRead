package org.develop.services.database;


import lombok.extern.java.Log;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private String serverUrl;
    private String dataBaseName;
    private boolean chargeTables;
    private String conURL;
    private String initScript;


    private DatabaseManager(){
        try {
            configFromProperties();
            openConnection();
            logger.debug("Connection Successfully");
            if (chargeTables){
                executeScript(initScript,false);
                logger.debug("Init Script Successfully");
            }
        }catch (SQLException | FileNotFoundException e) {
            logger.error("Error : " + e.getMessage(),e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseManager getInstance(){
        if (instance==null){
            instance=new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()){
            try{
                openConnection();
            }catch (SQLException e){
               logger.error("Error : " + e.getMessage(),e);
            }
        }
        return connection;
    }

    private void openConnection() throws SQLException{
        connection = DriverManager.getConnection(conURL);
    }

    public void closeConnection() throws SQLException{
        if (preparedStatement != null){ preparedStatement.close();}
        connection.close();
    }

    private void configFromProperties(){
        try{
        Properties properties = new Properties();
        properties.load(DatabaseManager.class.getClassLoader().getResourceAsStream("config.properties"));

        serverUrl= properties.getProperty("database.url","jdbc:sqlite");
        dataBaseName = properties.getProperty("database.name","Amet");
        chargeTables=Boolean.parseBoolean(properties.getProperty("database.initDatabase","false"));
        conURL =properties.getProperty("database.connectionUrl", serverUrl + ":"+dataBaseName + ".db");
        System.out.println(conURL);
        initScript=properties.getProperty("database.initScript","init.sql");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void executeScript(String script, boolean logWriter) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(connection);
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(script);
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            runner.setLogWriter(logWriter ? new PrintWriter(System.out) : null);
            runner.runScript(reader);
        } else {
            throw new FileNotFoundException("Script not found: " + script);
        }
    }


}