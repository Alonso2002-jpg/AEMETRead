# Proyecto AEMET - Java con SQLite
***
Este proyecto es una aplicación simple en Java que utiliza SQLite como base de datos. A continuación, se describen los pasos para configurar y ejecutar el proyecto.
## Requisitos
***
* Java 8 o superior
* Gradle
## Configuración
***

### Paso 1: Dependencias de Gradle
Agrega las siguientes dependencias a tu archivo `build.gradle`:

```kotlin
plugins {
id("java")
}

group = "org.develop"
version = "1.0-SNAPSHOT"

repositories {
mavenCentral()
}

dependencies {
testImplementation(platform("org.junit:junit-bom:5.9.1"))
testImplementation("org.junit.jupiter:junit-jupiter")
implementation("org.xerial:sqlite-jdbc:3.43.0.0")
implementation("org.projectlombok:lombok:1.18.30")
annotationProcessor("org.projectlombok:lombok:1.18.30")
implementation("com.google.code.gson:gson:2.10.1")
implementation("ch.qos.logback:logback-classic:1.4.11")
implementation("com.opencsv:opencsv:5.8")
implementation("org.apache.commons:commons-csv:1.10.0")
implementation("org.mybatis:mybatis:3.5.13")

}

tasks.test {
useJUnitPlatform()
}

tasks.jar{
manifest{
attributes["Main-class"] = "org.develop.Main"
}

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```

## Model
***
### Paso 2: Crear la clase Aemet
Crea una clase `Aemet` con los atributos `id`, LocalDate `actualDate`,`provincia`, `localidad`,`maxDegrees`, `minDegrees`,`precipitation`,LocalTime `maxTempHour`,LocalTime `minTempHour`.
```java
package org.develop.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Aemet {
    private Integer id;
    private LocalDate actualDate;
    private String provincia;
    private String localidad;
    private double maxDegrees;
    private double minDegrees;
    private double precipitation;

    private LocalTime maxTempHour;
    private LocalTime minTempHour;
}
```
## Controllers
***
### Paso 3: Crear la clase AEMETcontroller

La clase `AEMETcontroller` esta diseñada para realizar diversas operaciones relacionadas con datos meteorológicos almacenados en una lista de objetos de tipo `Aemet`.
El constructor de la clase recibe una lista de objetos `Aemet` como parámetro y la asigna a una variable de instancia `temps`.
* `dataProv(String provincia)` : Este método busca y devuelve una lista de datos meteorológicos para una provincia específica. Utiliza la programación funcional y el método stream para filtrar los datos correspondientes a la provincia dada.
* `maxminTempXDay()`: Calcula y muestra las temperaturas máximas y mínimas por día, así como la provincia donde ocurrieron estas temperaturas máximas y mínimas. Utiliza la programación funcional y el método stream junto con operaciones de agrupación y búsqueda.
* `maxTempXProvDay()`: Calcula y muestra la temperatura máxima por provincia y día. Utiliza la programación funcional y el método stream junto con operaciones de agrupación y búsqueda.
* `minTempXProvDay()`: Calcula y muestra la temperatura mínima por provincia y día. Utiliza la programación funcional y el método stream junto con operaciones de agrupación y búsqueda.
* `averageTempXProvDay()`: Calcula y muestra la temperatura media por provincias y día. Utiliza la programación funcional y el método stream junto con operaciones de agrupación y cálculo de la temperatura media.
* `pretMaxXDay()`: Calcula y muestra la precipitación máxima por día y la provincia donde ocurrió. Utiliza la programación funcional y el método stream junto con operaciones de agrupación y búsqueda de la precipitación máxima.
* `averagePrecipitationXDay()`: Calcula y muestra la precipitación media por provincias y día. Utiliza la programación funcional y el método stream junto con operaciones de agrupación y cálculo de la precipitación media.
* `withPrecipitation()`: Muestra lugares donde ha llovido agrupados por provincias y día. Utiliza la programación funcional y el método stream junto con operaciones de agrupación y filtrado.
* `mostPrecipitation()`: Muestra el lugar donde más ha llovido. Utiliza la programación funcional y el método stream para encontrar el lugar con la precipitación máxima.
* `getDatosProv(String provincia)`: Recopila y muestra datos relacionados con una provincia específica, incluyendo temperaturas máximas y mínimas, temperatura media, precipitación máxima, precipitación media, entre otros.

```java
package org.develop.services.controllers;

import org.develop.model.Aemet;
import org.develop.services.file.ReadCSVAemet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AEMETcontroller {
    private final Logger logger = LoggerFactory.getLogger(AEMETcontroller.class);
    List<Aemet> temps;

    public AEMETcontroller(List<Aemet> temps) {
        this.temps = temps;
    }

    public List<Aemet> dataProv(String provincia) {
        return temps.stream()
                .filter(pr -> provincia.equalsIgnoreCase(pr.getProvincia()))
                .toList();
    }

    public void maxminTempXDay() {
        logger.debug("¿Dónde se dio la temperatura máxima y mínima total en cada uno de los días?");
        var max = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getActualDate,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Aemet::getMaxDegrees)),
                                m -> "Maxima: " + m.map(Aemet::getProvincia).orElse("")
                        )
                ));

        var min = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getActualDate,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparingDouble(Aemet::getMinDegrees)),
                                m -> "Minima: " + m.map(Aemet::getProvincia).orElse("")
                        )
                ));

        max.forEach((a, b) -> System.out.println(a + " : " + b));
        min.forEach((a, b) -> System.out.println(a + " : " + b));
    }

    public void maxTempXProvDay() {
        logger.debug("Máxima temperatura agrupado por provincias y día.");
        var maxTe = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.toMap(Aemet::getActualDate, Aemet::getMaxDegrees,
                                Double::max))
                );
        maxTe.forEach((a, b) -> System.out.println(a + " : " + b));
    }

    public void minTempXProvDay() {
        logger.debug("Mínima temperatura agrupado por provincias y día.");
        var minTem = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.toMap(Aemet::getActualDate, Aemet::getMinDegrees,
                                Double::min))
                );
        minTem.forEach((a, b) -> System.out.println(a + " : " + b));
    }

    public void averageTempXProvDay() {
        logger.debug("Medía de temperatura agrupado por provincias y día.");
        var avTem = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Aemet::getActualDate),
                                tM -> tM.entrySet().stream()
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                tV -> tV.getValue().stream()
                                                        .mapToDouble(ae -> ae.getMaxDegrees() + ae.getMinDegrees())
                                                        .average()
                                                        .orElse(Double.NaN)
                                        ))
                        )
                ));
        avTem.forEach((a, b) -> System.out.println(a + " : " + b));
    }

    public void pretMaxXDay() {
        logger.debug("Precipitación máxima por días y dónde se dio.");
        var result = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getActualDate,
                        Collectors.toMap(
                                Aemet::getProvincia,
                                Aemet::getPrecipitation,
                                Math::max
                        )
                ));

        result.forEach((date, provincePrecipMap) -> {
            Optional<Map.Entry<String, Double>> maxPrecipitationEntry = provincePrecipMap.entrySet().stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue));

            maxPrecipitationEntry.ifPresent(stringDoubleEntry -> System.out.println(date + ": " + stringDoubleEntry.getKey() + " - " + stringDoubleEntry.getValue()));
        });

    }
    //Resto Codigo
}

```
### Paso 4: Crear la clase WriteJSONAemet
La clase `WriteJSONAemet` esta diseñada para escribir una lista de objetos `Aemet` en formato `JSON`  en un archivo. Proporciona un método llamado `writeJSON`  que toma el nombre del archivo de destino y la lista de objetos `Aemet` que se desea escribir en formato `JSON`.

```java
package org.develop.services.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.develop.adapter.LocalDateAdapter;
import org.develop.adapter.LocalTimeAdapter;
import org.develop.model.Aemet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

  public class WriteJSONAemet {
      
      public boolean writeJSON(String filename, List<Aemet> provData){
      // Ruta completa al archivo de destino
      String path = Paths.get("").toAbsolutePath() + File.separator + "data" +File.separator + filename;
      // Configuración de Gson para serializar LocalDate y LocalTime
      Gson gson = new GsonBuilder()
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
      .registerTypeAdapter(LocalTime.class,new LocalTimeAdapter())
      .setPrettyPrinting()
      .create();
      boolean success = false;

      try (FileWriter writer = new FileWriter(path)){
      gson.toJson(provData,writer);
      success=true;
      } catch (IOException e) {
      throw new RuntimeException(e);
      }
      return success;
      }
      }
```
### Paso 5: Crear la clase ReadCSVAemet
La clase `ReadCSVAemet` esta diseñada para leer archivos `CSV` y convertirlos en objetos de tipo `Aemet`. Ademas se utiliza las bibliotecas OpenCSV y Apache Commons CSV para realizar la lectura y conversión de archivos CSV, y luego crea objetos Aemet a partir de los datos. Esto permite procesar y utilizar datos meteorológicos almacenados en archivos CSV en nuestra aplicacion.
* `convertFileToUTF8(String inputFile, String outputFile)`: Método estático para convertir un archivo de codificación windows-1252 a UTF-8.
* `readFile(String nomFinch)`: Lee un archivo CSV y crea una lista de objetos Aemet a partir de los datos.
* `readFilesDays()`: Lee varios archivos CSV correspondientes a días específicos y crea una lista de objetos Aemet que contiene datos meteorológicos de varios días.

```java 
package org.develop.services.file;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.csv.CSVFormat;
import org.develop.model.Aemet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReadCSVAemet {

    // Ruta al directorio de datos
    String pathData=Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator;
    
    public static String convertFileToUTF8(String inputFile, String outputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "windows-1252"));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
        return outputFile;
    }
    
    public List<Aemet> readFile(String nomFinch){
        String path = pathData + nomFinch;
        List<Aemet> tempXDay = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm");
        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))){
            String[] line;

            //Saltamos la primera linea con la información
            reader.readNext();

            //Leeremos todos los datos del csv
            while ((line = reader.readNext()) != null) {
                String[] datos = line[0].split(";");

                Aemet aemet = new Aemet();
                aemet.setActualDate(LocalDate.parse(nomFinch.substring(5,13),formatter));
                if (datos.length > 1) {

                    aemet.setLocalidad(new String(datos[0].getBytes(StandardCharsets.UTF_8)));
                    aemet.setProvincia(new String(datos[1].getBytes(StandardCharsets.UTF_8)));
                    aemet.setMaxDegrees(Double.parseDouble(datos[2]));
                    aemet.setMaxTempHour(LocalTime.parse(datos[3].length() < 5 ? "0"+datos[3]:datos[3],formatter2));
                    aemet.setMinDegrees(Double.parseDouble(datos[4]));
                    aemet.setMinTempHour(LocalTime.parse(datos[5].length() < 5 ? "0"+datos[5]:datos[5],formatter2));
                    aemet.setPrecipitation(Double.parseDouble(datos[6]));

                    tempXDay.add(aemet);
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return tempXDay;
    }
    
    public ArrayList<Aemet> readFilesDays() throws IOException {
        ArrayList<Aemet> days = new ArrayList<>();

        // Leer y agregar datos de varios archivos CSV de días específicos
        days.addAll(readFile(ReadCSVAemet.convertFileToUTF8(pathData + "Aemet20171029.csv","Aemet20171029r.csv")));
        days.addAll(readFile(ReadCSVAemet.convertFileToUTF8(pathData + "Aemet20171030.csv","Aemet20171030r.csv")));
        days.addAll(readFile(ReadCSVAemet.convertFileToUTF8(pathData + "Aemet20171031.csv","Aemet20171031r.csv")));
        return days;
    }

    public static void main(String[] args) throws IOException {
        ReadCSVAemet rs = new ReadCSVAemet();
        rs.readFilesDays().forEach(System.out::println);
    }
}
```
## Services
***
### Paso 6: Crear la clase DatabaseManager
La clase `DatabaseManager` se utiliza para gestionar una base de datos y proporciona métodos para abrir y cerrar conexiones, configurar la base de datos a partir de un archivo de propiedades y ejecutar scripts `SQL` en la base de datos.

```java 
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
```
## Ejecucion
***
### Paso 7: Iniciar la Base de Datos
En el método `main`, obtén la instancia de `DatabaseManager` y usa el método `executeScript` para ejecutar un script SQL
desde un archivo:

```java
public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        DatabaseManager db = DatabaseManager.getInstance();
        //El executeScript se ejecuta automaticamente ya que asi se establece en el fichero de propiedades.
    }
}
```
## Repository
***
### Paso 8: Crear la clase AEMETrepositoryImpl
La clase `AEMETrepositoryImpl` es una implementación de la interfaz `AEMETrepository` que proporciona métodos para interactuar con una base de datos. Donde esta clase proporciona una capa de abstracción para interactuar con la base de datos y realizar operaciones `CRUD` (Crear, Leer, Actualizar, Eliminar) en objetos `Aemet` almacenados en la base de datos.

```java

package org.develop.repository;

import org.develop.model.Aemet;
import org.develop.services.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AEMETrepositoryImpl implements AEMETrepository<Aemet, Integer> {
    private static AEMETrepositoryImpl instance;
    private final DatabaseManager db;

    private final Logger logger = LoggerFactory.getLogger(AEMETrepositoryImpl.class);
    
    private AEMETrepositoryImpl(DatabaseManager db){this.db = db;}
    
    public static AEMETrepositoryImpl getInstance(DatabaseManager db){
        if (instance==null){
            instance=new AEMETrepositoryImpl(db);
        }
        return instance;
    }
    
    @Override
    public Aemet save(Aemet aemet) throws SQLException {
        logger.debug("Saving Objeto AEMET en la Base de Datos");
        String SQLquery = "INSERT INTO Aemet (actualDate, provincia, localidad, maxDegrees, minDegrees, precipitation, maxTempHour, minTempHour)" +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement(SQLquery)){
            stmt.setDate(1, Date.valueOf(aemet.getActualDate()));
            stmt.setString(2,aemet.getProvincia());
            stmt.setString(3,aemet.getLocalidad());
            stmt.setDouble(4,aemet.getMaxDegrees());
            stmt.setDouble(5,aemet.getMinDegrees());
            stmt.setDouble(6,aemet.getPrecipitation());
            stmt.setString(7,aemet.getMaxTempHour().toString());
            stmt.setString(8,aemet.getMinTempHour().toString());
            var res = stmt.executeUpdate();

            if (res > 0){
               var stmt2 = conn.prepareStatement("SELECT last_insert_rowid()");
               ResultSet rs = stmt2.executeQuery();
                if (rs.next()) {
                    aemet.setId(rs.getInt(1));
                }
                rs.close();
            }
        }catch (SQLException e) {
            logger.error("Error guardando Objeto: " + e.getMessage(),e);
        }

        return aemet;
    }
    
    @Override
    public Aemet update(Aemet aemet) throws SQLException {
        logger.debug("Updating Object Aemet con ID: " + aemet.getId());
        String SQLquery = "UPDATE Aemet SET maxDegrees = ?, minDegrees = ?, precipitation = ?, maxTempHour = ?, minTempHour= ? WHERE id = ?";

        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(SQLquery)){

            stmt.setDouble(1,aemet.getMaxDegrees());
            stmt.setDouble(2,aemet.getMinDegrees());
            stmt.setDouble(3,aemet.getPrecipitation());
            stmt.setString(4,aemet.getMaxTempHour().toString());
            stmt.setString(5,aemet.getMinTempHour().toString());
            stmt.setInt(6,aemet.getId());
            stmt.executeUpdate();
        }catch (SQLException e) {
            logger.error("Error Actualizando Objeto: " + e.getMessage(),e);
        }

        return aemet;
    }
    
    @Override
    public Optional<Aemet> findById(Integer id){
        logger.debug("Finding Object AEMET with ID: " + id);
        String SQLquery = "SELECT * FROM Aemet WHERE id = ?";
        Optional<Aemet> aemetOpt = Optional.empty();

        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(SQLquery)){
            stmt.setInt(1,id);
            var rs = stmt.executeQuery();

            while (rs.next()){
                Aemet aemet = new Aemet();
                aemet.setId(rs.getInt("id"));
                aemet.setActualDate(rs.getDate("actualdate").toLocalDate());
                aemet.setProvincia(rs.getString("provincia"));
                aemet.setLocalidad(rs.getString("localidad"));
                aemet.setMaxDegrees(rs.getDouble("maxDegrees"));
                aemet.setMinDegrees(rs.getDouble("minDegrees"));
                aemet.setPrecipitation(rs.getDouble("precipitation"));
                aemet.setMaxTempHour(LocalTime.parse(rs.getString("maxTempHour")));
                aemet.setMinTempHour(LocalTime.parse(rs.getString("minTempHour")));
                aemetOpt=Optional.of(aemet);
            }
        }catch (SQLException e) {
            logger.error("Error Buscando Objeto: " + e.getMessage(),e);
        }
        return aemetOpt;
    }

    @Override
    public List<Aemet> findAll(){
        logger.debug("Obteniedo Todos los Datos de la Base de datos");
        String SQLquery = "SELECT * FROM Aemet";
        List<Aemet> tempDays=new ArrayList<>();
        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(SQLquery)){
            var rs = stmt.executeQuery();
            while (rs.next()){
                Aemet aemet = new Aemet();
                aemet.setId(rs.getInt("id"));
                aemet.setActualDate(rs.getDate("actualdate").toLocalDate());
                aemet.setProvincia(rs.getString("provincia"));
                aemet.setLocalidad(rs.getString("localidad"));
                aemet.setMaxDegrees(rs.getDouble("maxDegrees"));
                aemet.setMinDegrees(rs.getDouble("minDegrees"));
                aemet.setPrecipitation(rs.getDouble("precipitation"));
                aemet.setMaxTempHour(LocalTime.parse(rs.getString("maxTempHour")));
                aemet.setMinTempHour(LocalTime.parse(rs.getString("minTempHour")));
                tempDays.add(aemet);
            }
        }catch (SQLException e) {
            logger.error("Error Buscando Objeto: " + e.getMessage(),e);
        }
        return tempDays;
    }
    
    @Override
    public boolean deleteById(Integer id){
        logger.debug("Deleting Object with ID: " + id);
        String SQLquery = "DELETE FROM Aemet WHERE id = ?";
        boolean success = false;
        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(SQLquery)){
            stmt.setInt(1,id);
            var rs = stmt.executeUpdate();
            if (rs>0){
                success=true;
            }
        }catch (SQLException e){
            logger.error("Error while deleting: " + e.getMessage(),e);
        }

        return success;
    }
    
    @Override
    public void deleteAll(){
        logger.debug("Deleting All Objects from Aemet");
        String SQLquery = "DELETE FROM Aemet";
        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(SQLquery)){
            var rs = stmt.executeUpdate();
        }catch (SQLException e){
            logger.error("Error while deleting: " + e.getMessage(),e);
        }
    }
    
    @Override
    public List<Aemet> findByLocalidad(String localidad) {
        logger.debug("Finding by Localidad what contains: " + localidad);
        String SQLquery = "SELECT * FROM Aemet WHERE localidad LIKE ?";
        List<Aemet> rsList = new ArrayList<Aemet>();
        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(SQLquery)){
            stmt.setString(1,"%"+localidad +"%");
            var rs = stmt.executeQuery();
            while (rs.next()){
                Aemet aemet = new Aemet();
                aemet.setId(rs.getInt("id"));
                aemet.setActualDate(rs.getDate("actualDate").toLocalDate());
                aemet.setProvincia(rs.getString("provincia"));
                aemet.setLocalidad(rs.getString("localidad"));
                aemet.setMaxDegrees(rs.getDouble("maxDegrees"));
                aemet.setMinDegrees(rs.getDouble("minDegrees"));
                aemet.setPrecipitation(rs.getDouble("precipitation"));
                aemet.setMaxTempHour(LocalTime.parse(rs.getString("maxTempHour")));
                aemet.setMinTempHour(LocalTime.parse(rs.getString("minTempHour")));
                rsList.add(aemet);
            }
        }catch (SQLException e){
            logger.error("Error while getting by name: " + e.getMessage(),e);
        }
        return rsList;
    }
}
```
Y luego tu main donde quieras.

```java
package org.develop;

import org.develop.model.Aemet;
import org.develop.repository.AEMETrepositoryImpl;
import org.develop.services.controllers.AEMETcontroller;
import org.develop.services.database.DatabaseManager;
import org.develop.services.file.ReadCSVAemet;
import org.develop.services.file.WriteJSONAemet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        DatabaseManager db = DatabaseManager.getInstance();
        AEMETrepositoryImpl aemetImpl = AEMETrepositoryImpl.getInstance(db);
        ReadCSVAemet rs = new ReadCSVAemet();

        //Guardadndo los datos del fichero CSV en la Base de Datos.
        rs.readFilesDays().forEach(file -> {
            try {
                aemetImpl.save(file);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Actualizando Objeto de la base de datos
//        Aemet obj1 = aemetImpl.findById(5).get();
//        System.out.println(obj1);
//        obj1.setPrecipitation(1.20);
//        var obj11=aemetImpl.update(obj1);
//        System.out.println(obj11);
        //Obteniendo todos los Objetos de la Base de datos.
        //aemetImpl.findAll().forEach(System.out::println);

        //Obteniendo Objeto por ID
//        var obj2 = aemetImpl.findById(1);
//        System.out.println(obj2);

        //Obteniendo Objeto por Localidad
//        var obj3 = aemetImpl.findByLocalidad("ñ");
//        obj3.forEach(System.out::println);

        //Eliminando Objeto por ID
        //var suc = aemetImpl.deleteById(5);
        //System.out.println(suc);
        //Eliminando Todos los Objetos de la Bsee Datos
        //aemetImpl.deleteAll();

        //Implementacion CONTROLLER STREAMS.
        var list = aemetImpl.findAll();
        AEMETcontroller aemeTcontroller = new AEMETcontroller(list);
        aemeTcontroller.ejecutarOperaciones();
        aemeTcontroller.getDatosProv("Burgos");

        var listProv = aemeTcontroller.dataProv("Madrid");
        WriteJSONAemet write = new WriteJSONAemet();
        System.out.println(write.writeJSON("MadridData.json",listProv));
    }
}
```

## Adapter
***
### Paso 9: Crear la clase LocalDateAdapter
La clase `LocalDateAdapter` se utiliza para asegurarse de que los objetos `LocalDate` se serialicen correctamente a formato `JSON` con el patrón de fecha especificado cuando se utilizan con la biblioteca `Gson`.
```java
package org.develop.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }
    
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return null;
    }
}
```

***
### Paso 10: Crear la clase LocalTimeAdapter
La clase `LocalTimeAdapter` se utiliza para asegurarse de que los objetos `LocalTime` se serialicen correctamente a formato `JSON` con el patrón de hora especificado cuando se utilizan con la biblioteca `Gson`.

```java
package org.develop.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void write(JsonWriter out, LocalTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }
    
    @Override
    public LocalTime read(JsonReader in) throws IOException {
        return null;
    }
}

```




