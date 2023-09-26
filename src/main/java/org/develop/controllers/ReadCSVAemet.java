package org.develop.controllers;

import com.opencsv.CSVReader;
import org.develop.model.Day;
import org.develop.model.Localidad;
import org.develop.model.Provincia;
import org.develop.model.Temperatura;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ReadCSVAemet {
    String path;
    public ArrayList<Day> readFile(String nomFinch){
         path= Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + nomFinch;
        ArrayList<Day> days = new ArrayList<Day>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        Day day = new Day();
        day.setDate(LocalDateTime.parse(nomFinch.substring(5,13),formatter));

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))){
            String[] line;
            //Saltamos la primera linea con la información
            reader.readNext();

            //Leeremos todos los datos del csv
            while ((line = reader.readNext()) != null) {
                String[] datos = line[0].split(";");

                Provincia provincia = null;
                if (datos.length > 1) {
                    Localidad localidad = new Localidad();
                    provincia = new Provincia();
                    Temperatura maxTemp = new Temperatura();
                    Temperatura minTemp = new Temperatura();
                    localidad.setName(datos[0]);
                    provincia.setName(datos[1]);
                    maxTemp.setDegrees(Double.parseDouble(datos[2]));
                    maxTemp.setHour(datos[3]);
                    localidad.setMaxTemperature(maxTemp);
                    minTemp.setDegrees(Double.parseDouble(datos[4]));
                    minTemp.setHour(datos[5]);
                    localidad.setMinTemperature(minTemp);
                    localidad.setPrecipitation(Double.parseDouble(datos[6]));
                    if (day.getProvincias().stream().map(Provincia::getName).noneMatch(pr-> pr.equalsIgnoreCase(datos[1]))) {
                        provincia.getLocalities().add(localidad);
                        day.getProvincias().add(provincia);
                    }else{
                        var prov = day.getProvincias().stream().filter(mp -> mp.getName().equals(datos[1])).findAny();
                        prov.get().getLocalities().add(localidad);
                    }
                } else {
                }
            days.add(day);
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return days;
    }

    public static void main(String[] args) {
        ReadCSVAemet recv = new ReadCSVAemet();
        Day dia = recv.readFile("Aemet20171029.csv").get(0);
        dia.getProvincias().forEach(System.out::println);
    }
}
