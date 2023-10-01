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
        String path = Paths.get("").toAbsolutePath() + File.separator + "data" +File.separator + filename;
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
