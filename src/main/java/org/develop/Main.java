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
        var obj3 = aemetImpl.findByLocalidad("ñ");
        obj3.forEach(System.out::println);

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