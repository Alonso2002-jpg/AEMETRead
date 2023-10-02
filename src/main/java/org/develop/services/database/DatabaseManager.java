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
/**
 * Esta clase gestiona la conexion a una base de datos SQLite y proporciona funciones para ejecutar scripts SQL.
 */
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


    /**
     * Constructor privado para crear una instancia unica de DatabaseManager.
     */
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

    /**
     * Obtiene una instancia unica de DatabaseManager.
     *
     * @return La instancia unica de DatabaseManager.
     */
    public static DatabaseManager getInstance(){
        if (instance==null){
            instance=new DatabaseManager();
        }
        return instance;
    }

    /**
     * Obtiene una conexion a la base de datos.
     * @return La conexion a la base de datos.
     * @throws SQLException Si ocurre un error al abrir la conexion.
     */
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

    /**
     * Abre una conexion a la base de datos.
     * @throws SQLException Si ocurre un error al abrir la conexion.
     */
    private void openConnection() throws SQLException{
        connection = DriverManager.getConnection(conURL);
    }


    /**
     * Cierra la conexión a la base de datos.
     * @throws SQLException Si ocurre un error al cerrar la conexion.
     */
    public void closeConnection() throws SQLException{
        if (preparedStatement != null){ preparedStatement.close();}
        connection.close();
    }

    /**
     * Carga la configuracion de la base de datos desde un archivo de propiedades.
     * Lee las propiedades de conexion, nombre de la base de datos, etc.
     */
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

    /**
     * Ejecuta un script SQL en la base de datos.
     * @param script     El nombre del archivo de script a ejecutar.
     * @param logWriter  Indica si se debe registrar la salida en la consola.
     * @throws IOException Si ocurre un error al leer el archivo de script.
     * @throws SQLException Si ocurre un error al ejecutar el script en la base de datos.
     */
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