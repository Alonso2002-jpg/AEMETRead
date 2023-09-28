package org.develop.services.file;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.develop.model.Aemet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReadCSVAemet {
    public List<Aemet> readFile(String nomFinch){
        String path = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + nomFinch;
        List<Aemet> tempXDay = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

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
                    aemet.setLocalidad(datos[0]);
                    aemet.setProvincia(datos[1]);
                    aemet.setMaxDegrees(Double.parseDouble(datos[2]));
                    aemet.setMaxTempHour(datos[3]);
                    aemet.setMinDegrees(Double.parseDouble(datos[4]));
                    aemet.setMinTempHour(datos[5]);
                    aemet.setPrecipitation(Double.parseDouble(datos[6]));

                    tempXDay.add(aemet);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return tempXDay;
    }

    public ArrayList<List<Aemet>> readFilesDays() {
        ArrayList<List<Aemet>> days = new ArrayList<>();
        days.add(readFile("Aemet20171029.csv"));
        days.add(readFile("Aemet20171030.csv"));
        days.add(readFile("Aemet20171031.csv"));
        return days;
    }

    public static void main(String[] args) {
        ReadCSVAemet rs = new ReadCSVAemet();
        rs.readFilesDays().forEach(System.out::println);
    }
}
