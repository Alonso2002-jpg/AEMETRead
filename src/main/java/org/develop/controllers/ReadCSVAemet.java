package org.develop.controllers;

import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class ReadCSVAemet {
    String path;
    public void reafFile(){
         path= Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "Aemet20171029.csv";

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))){
            String[] line;
            //Saltamos la primera linea con la información
            reader.readNext();

            //Leeremos todos los datos del csv
            while ((line = reader.readNext()) != null){
                String[] datos = line[0].split(";");
                System.out.println(datos.length);

            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReadCSVAemet recv = new ReadCSVAemet();
        recv.reafFile();
    }
}
