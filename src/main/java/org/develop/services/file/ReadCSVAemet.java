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

/**
 * Esta clase se encarga de leer archivos CSV y convertirlos en objetos Aemet.
 */
public class ReadCSVAemet {

    // Ruta al directorio de datos
    String pathData=Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator;

    /**
     * Convierte un archivo a codificación UTF-8.
     * @param inputFile  El archivo de entrada con codificacion windows-1252.
     * @param outputFile El archivo de salida con codificacion UTF-8.
     * @return La ruta al archivo de salida con codificacion UTF-8.
     * @throws IOException Si ocurre un error durante la conversion.
     */
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

    /**
     * Lee un archivo CSV y devuelve una lista de objetos Aemet.
     * @param nomFinch El nombre del archivo CSV a leer.
     * @return Una lista de objetos Aemet.
     */
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

    /**
     * Lee varios archivos CSV de dias especificos y devuelve una lista de objetos Aemet.
     * @return Una lista de objetos Aemet de varios archivos CSV.
     * @throws IOException Si ocurre un error durante la lectura de archivos.
     */
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
