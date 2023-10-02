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

/**
 * Implementacion de la interfaz AEMETrepository que proporciona metodos para interactuar con la base de datos.
 */
public class AEMETrepositoryImpl implements AEMETrepository<Aemet, Integer> {
    private static AEMETrepositoryImpl instance;
    private final DatabaseManager db;

    private final Logger logger = LoggerFactory.getLogger(AEMETrepositoryImpl.class);

    /**
     * Constructor privado para crear una instancia de AEMETrepositoryImpl.
     * @param db El administrador de la base de datos.
     */
    private AEMETrepositoryImpl(DatabaseManager db){this.db = db;}

    /**
     * Obtiene una instancia unica de AEMETrepositoryImpl.
     * @param db El administrador de la base de datos.
     * @return La instancia unica de AEMETrepositoryImpl.
     */
    public static AEMETrepositoryImpl getInstance(DatabaseManager db){
        if (instance==null){
            instance=new AEMETrepositoryImpl(db);
        }
        return instance;
    }

    /**
     * Guarda un objeto Aemet en la base de datos.
     * @param aemet El objeto Aemet que se va a guardar.
     * @return El objeto Aemet despues de ser guardado en la base de datos con su identificador unico actualizado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
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

    /**
     * Actualiza un objeto Aemet en la base de datos con la informacion proporcionada en el objeto Aemet dado.
     * @param aemet El objeto Aemet que se va a actualizar en la base de datos.
     * @return El objeto Aemet despues de ser actualizado en la base de datos.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
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

    /**
     * Busca un objeto Aemet en la base de datos por su identificador unico.
     * @param id El identificador unico del objeto Aemet que se desea buscar.
     * @return Un objeto Optional que contiene el objeto Aemet encontrado, o un Optional vacio si no se encuentra ningun objeto con el ID dado.
     */
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

    /**
     * Obtiene todos los objetos Aemet almacenados en la base de datos.
     * @return Una lista de objetos Aemet que representan todos los datos de la base de datos.
     */
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

    /**
     * Elimina un objeto Aemet de la base de datos por su identificador unico.
     * @param id El identificador unico del objeto Aemet que se desea eliminar.
     * @return `true` si se elimina con exito, `false` si no se encuentra ningun objeto con el ID dado o si ocurre un error al eliminar.
     */
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

    /**
     * Elimina todos los objetos Aemet almacenados en la base de datos.
     */
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


    /**
     * Busca objetos Aemet en la base de datos por el nombre de la localidad que contiene la cadena especificada.
     * @param localidad La cadena que se utiliza para buscar objetos Aemet por el nombre de la localidad.
     * @return Una lista de objetos Aemet que contienen la cadena especificada en el nombre de la localidad.
     */
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
