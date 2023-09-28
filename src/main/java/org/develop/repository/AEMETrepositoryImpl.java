package org.develop.repository;

import org.develop.model.Aemet;
import org.develop.services.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
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
        String SQLquery = "INSERT INTO Aemet (date, provincia, localidad, maxDegrees, minDegrees, precipitation, maxTempHour, minTempHour)" +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS)){
            stmt.setDate(1, Date.valueOf(aemet.getActualDate()));
            stmt.setString(2,aemet.getProvincia());
            stmt.setString(3,aemet.getLocalidad());
            stmt.setDouble(4,aemet.getMaxDegrees());
            stmt.setDouble(5,aemet.getMinDegrees());
            stmt.setDouble(6,aemet.getPrecipitation());
            stmt.setString(7,aemet.getMaxTempHour());
            stmt.setString(8,aemet.getMinTempHour());
            var res = stmt.executeUpdate();

            if (res > 0){
                var rs = stmt.getGeneratedKeys();
                if (rs.next()){
                    aemet.setId(rs.getInt(1));
                }
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
            stmt.setString(4,aemet.getMaxTempHour());
            stmt.setString(5,aemet.getMinTempHour());
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
                aemet.setActualDate(rs.getDate("actual_date").toLocalDate());
                aemet.setProvincia(rs.getString("provincia"));
                aemet.setLocalidad(rs.getString("localidad"));
                aemet.setMaxDegrees(rs.getDouble("maxDegrees"));
                aemet.setMinDegrees(rs.getDouble("minDegrees"));
                aemet.setPrecipitation(rs.getDouble("precipation"));
                aemet.setMaxTempHour(rs.getString("maxTempHour"));
                aemet.setMinTempHour(rs.getString("minTempHour"));
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
                aemet.setActualDate(rs.getDate("actual_date").toLocalDate());
                aemet.setProvincia(rs.getString("provincia"));
                aemet.setLocalidad(rs.getString("localidad"));
                aemet.setMaxDegrees(rs.getDouble("maxDegrees"));
                aemet.setMinDegrees(rs.getDouble("minDegrees"));
                aemet.setPrecipitation(rs.getDouble("precipation"));
                aemet.setMaxTempHour(rs.getString("maxTempHour"));
                aemet.setMinTempHour(rs.getString("minTempHour"));
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
    public List<Aemet> findByName(String name) {
        logger.debug("Finding by name: " + name);
        String SQLquery = "SELECT * FROM Aemet WHERE name: ?";
        List<Aemet> rsList = new ArrayList<Aemet>();
        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(SQLquery)){
            var rs = stmt.executeQuery();
            while (rs.next()){
                Aemet aemet = new Aemet();
                aemet.setId(rs.getInt("id"));
                aemet.setActualDate(Date.valueOf(rs.getString("actualDate")).toLocalDate());
                aemet.setProvincia(rs.getString("provincia"));
                aemet.setLocalidad(rs.getString("localidad"));
                aemet.setMaxDegrees(rs.getDouble("maxDegrees"));
                aemet.setMinDegrees(rs.getDouble("minDegrees"));
                aemet.setMaxTempHour(rs.getString("maxTempHour"));
                aemet.setMinTempHour(rs.getString("minTempHour"));
                rsList.add(aemet);
            }
        }catch (SQLException e){
            logger.error("Error while getting by name: " + e.getMessage(),e);
        }
        return rsList;
    }
}
