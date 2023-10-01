package org.develop.services.controllers;

import org.develop.model.Aemet;
import org.develop.services.file.ReadCSVAemet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AEMETcontroller {

    List<Aemet> temps;

    AEMETcontroller(List<Aemet> temps) {
        this.temps = temps;
    }

    public Map<String, Aemet> tempXDay(){
        return null;
    }

    public void maxminTempXDay(){
        var max =  temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getActualDate,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Aemet::getMaxDegrees)),
                                m -> "Maxima: " +  m.map(Aemet::getProvincia).orElse("")
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

        max.forEach((a,b)-> System.out.println(a + " : " + b));
        min.forEach((a,b)-> System.out.println(a + " : " + b));
    }

    public Map<String,Map<LocalDate,Double>> maxTempXProvDay(){
        return temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.toMap(Aemet::getActualDate,Aemet::getMaxDegrees,
                                Double::max))
                );
    }

    public Map<String,Map<LocalDate,Double>> minTempXProvDay(){
        return temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.toMap(Aemet::getActualDate,Aemet::getMinDegrees,
                                Double::min))
                );
    }

    public Map<String, Double> averageTempXProvDay(){

        return temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.collectingAndThen(
                        Collectors.mapping(a -> a.getMaxDegrees() + a.getMinDegrees(), Collectors.toList()),
                        list -> list.stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                        )
                ));
    }
    public Optional<Aemet> maxPrecipitation(){

        return temps.stream()
                .filter(pr -> pr.getPrecipitation() == temps.stream()
                        .mapToDouble(Aemet::getPrecipitation)
                        .max()
                        .getAsDouble())
                .findFirst();
    }

    public static void main(String[] args) throws IOException {
        ReadCSVAemet rs = new ReadCSVAemet();
        AEMETcontroller ae = new AEMETcontroller(rs.readFilesDays());
//        ae.maxTempXProvDay().forEach((a,b)-> System.out.println(a + " " + b));
//        System.out.println("------------------------------------------------");
//        ae.minTempXProvDay().forEach((a,b)-> System.out.println(a + " " + b));
//        ae.averageTempXProvDay().forEach((a,b)-> System.out.println(a + " " + b));
//        ae.maxminTempXDay();
        ae.averageTempXProvDay().forEach((a,b)-> System.out.println(a + " : " + b));
    }
}
