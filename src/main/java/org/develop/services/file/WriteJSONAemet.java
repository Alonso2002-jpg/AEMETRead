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

/**
 * Esta clase se encarga de escribir una lista de objetos Aemet en formato JSON en un archivo.
 */
public class WriteJSONAemet {

    /**
     * Escribe una lista de objetos Aemet en formato JSON en un archivo.
     * @param filename  El nombre del archivo en el que se escribiran los datos JSON.
     * @param provData  La lista de objetos Aemet que se desea escribir en el archivo JSON.
     * @return `true` si la escritura en el archivo es exitosa, `false` si ocurre algun error.
     */
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
