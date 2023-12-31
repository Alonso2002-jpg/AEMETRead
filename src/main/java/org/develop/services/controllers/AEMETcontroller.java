package org.develop.services.controllers;

import org.develop.model.Aemet;
import org.develop.services.file.ReadCSVAemet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * La clase AEMETcontroller proporciona metodos para analizar y procesar datos meteorologicos.
 * Permite realizar diversas operaciones, como buscar datos por provincia, encontrar temperaturas maximas y minimas,
 * calcular promedios de temperatura y precipitacion, identificar lugares donde ha llovido y mas.
 * Los resultados de estas operaciones se pueden mostrar o utilizar según sea necesario.
 */
public class AEMETcontroller {
    private final Logger logger = LoggerFactory.getLogger(AEMETcontroller.class);
    List<Aemet> temps;

    public AEMETcontroller(List<Aemet> temps) {
        this.temps = temps;
    }

    /**
     * Busca y devuelve una lista de datos meteorologicos para una provincia especifica.
     * @param provincia La provincia para la que se desean buscar los datos meteorologicos.
     * @return Lista de datos meteorologicos correspondientes a la provincia especificada.
     */
    public List<Aemet> dataProv(String provincia){
        return temps.stream()
                .filter(pr -> provincia.equalsIgnoreCase(pr.getProvincia()))
                .toList();
    }

    /**
     * Realiza un calculo de las temperaturas maximas y minimas por dia y muestra los resultados.
     */
    public void maxminTempXDay(){
        logger.debug("¿Dónde se dio la temperatura máxima y mínima total en cada uno de los días?");
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

    /**
     * Calcula y muestra la temperatura maxima por provincia y dia.
     */
    public void maxTempXProvDay(){
        logger.debug("Máxima temperatura agrupado por provincias y día.");
        var maxTe = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.toMap(Aemet::getActualDate,Aemet::getMaxDegrees,
                                Double::max))
                );
        maxTe.forEach((a,b)-> System.out.println(a + " : " + b));
    }


    /**
     * Calcula y muestra la temperatura minima por provincia y dia.
     */
    public void minTempXProvDay(){
        logger.debug("Mínima temperatura agrupado por provincias y día.");
        var minTem= temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getProvincia,
                        Collectors.toMap(Aemet::getActualDate,Aemet::getMinDegrees,
                                Double::min))
                );
        minTem.forEach((a,b)-> System.out.println(a + " : " + b));
    }

    /**
     * Calcula y muestra la temperatura media por provincias y dia.
     */
    public void averageTempXProvDay(){
    logger.debug("Medía de temperatura agrupado por provincias y día.");
            var avTem= temps.stream()
                .collect(Collectors.groupingBy(
                    Aemet::getProvincia,
                    Collectors.collectingAndThen(
                        Collectors.groupingBy(Aemet::getActualDate),
                        tM -> tM.entrySet().stream()
                            .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                tV -> tV.getValue().stream()
                                    .mapToDouble(ae -> ae.getMaxDegrees() + ae.getMinDegrees())
                                    .average()
                                    .orElse(Double.NaN)
                ))
        )
    ));
            avTem.forEach((a,b) -> System.out.println(a + " : " + b));
    }

    /**
     * Calcula y muestra la precipitación maxima por dia y la provincia donde ocurrio.
     */
    public void pretMaxXDay(){
        logger.debug("Precipitación máxima por días y dónde se dio.");
            var result = temps.stream()
    .collect(Collectors.groupingBy(
        Aemet::getActualDate,
        Collectors.toMap(
            Aemet::getProvincia,
            Aemet::getPrecipitation,
                Math::max
        )
    ));

    result.forEach((date, provincePrecipMap) -> {
        Optional<Map.Entry<String, Double>> maxPrecipitationEntry = provincePrecipMap.entrySet().stream()
            .max(Comparator.comparingDouble(Map.Entry::getValue));

    maxPrecipitationEntry.ifPresent(stringDoubleEntry -> System.out.println(date + ": " + stringDoubleEntry.getKey() + " - " + stringDoubleEntry.getValue()));
    });

    }

    /**
     * Calcula y muestra la precipitacion media por provincias y dia.
     */
    public void averagePrecipitationXDay (){
        logger.debug("Precipitación media por provincias y día.");
        var ab = temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getActualDate,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Aemet::getProvincia),
                                tP -> tP.entrySet().stream()
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                tPV -> tPV.getValue().stream()
                                                        .mapToDouble(Aemet::getPrecipitation)
                                                        .average()
                                                        .orElse(Double.NaN)
                                        ))
                        )
                ));
        ab.forEach((a,b)-> System.out.println(a + " : " + b));
    }


    /**
     * Muestra lugares donde ha llovido agrupados por provincias y dia.
     */
    public void withPrecipitation (){
        logger.debug("Lugares donde ha llovido agrupado por provincias y día.");
        var withPre= temps.stream()
                .collect(Collectors.groupingBy(
                        Aemet::getActualDate,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Aemet::getProvincia),
                                tP -> tP.entrySet().stream()
                                        .filter(pva -> pva.getValue().stream()
                                                .anyMatch(pv -> pv.getPrecipitation()> 0))
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                tPV -> tPV.getValue().stream()
                                                        .filter(tV -> tV.getPrecipitation() > 0)
                                                        .map(Aemet::getLocalidad)
                                                        .toList()
                                        ))
                        )
                ));
        withPre.forEach((a,b) -> System.out.println(a + " : " + b));
    }

    public void mostPrecipitation(){
        logger.debug("Lugar donde más ha llovido.");
        var mostPrec=temps.stream()
                .filter(ae -> ae.getPrecipitation() == temps.stream().mapToDouble(Aemet::getPrecipitation).max().orElse(0.0))
                .findFirst()
                .orElse(new Aemet());
        System.out.println(mostPrec.getProvincia() + " : " + mostPrec.getLocalidad());
    }

    /**
     * Recopila y muestra datos relacionados con la provincia especificada.
     * @param provincia El nombre de la provincia de la que se desea obtener los datos.
     */
    public void getDatosProv(String provincia){
    logger.debug("Cargando Datos de la Provincia "+ provincia);
    logger.debug("Datos por dia");
        //DATOS POR DIA
        var datXDay= temps.stream()
                .filter(ae -> ae.getProvincia().equals(provincia))
                .collect(Collectors.groupingBy(Aemet::getActualDate));
        datXDay.forEach((a,b) -> System.out.println(a + " : " + b));
    logger.debug("Temperatura maxima y minima segun localidad");
    //TEMPERATURA MAXIMA Y MINIMA - SEGUN LOCALIDAD
        var max =  temps.stream()
            .filter(ae -> ae.getProvincia().equals(provincia))
            .max(Comparator.comparingDouble(Aemet::getMaxDegrees))
                .orElse(new Aemet());
        System.out.println("Temperatura Maxima en: " + max.getLocalidad() + " = " + max.getMaxDegrees());
        var min = temps.stream()
            .filter(ae -> ae.getProvincia().equals(provincia))
            .min(Comparator.comparingDouble(Aemet::getMinDegrees))
                .orElse(new Aemet());
        System.out.println("Temperatura Mimima en: " + min.getLocalidad() + " = " + min.getMinDegrees());
    logger.debug("Temperatura Media Maxima");
    //TEMPERATURA MEDIA MAXIMA
        var mediaMax = temps.stream()
                .filter(ae -> ae.getProvincia().equals(provincia))
                .mapToDouble(Aemet::getMaxDegrees)
                .average()
                .orElse(0.0);
        System.out.println("Temperatura Media Maxima: " + mediaMax);
    logger.debug("Temperatura Media Minima");
    //TEMPERATURA MEDIA MINIMA
        var mediaMin = temps.stream()
                .filter(ae -> ae.getProvincia().equals(provincia))
                .mapToDouble(Aemet::getMinDegrees)
                .average()
                .orElse(0.0);
        System.out.println("Temperatura Media Minima: " + mediaMin);
    logger.debug("Precipitacion Maxima y en donde fue");
    //Precipitacion Maxima y en donde fue.
        var preciMax = temps.stream()
                .filter(ae -> ae.getProvincia().equals(provincia))
                .filter(ael -> ael.getPrecipitation() == temps.stream().filter(ae->ae.getProvincia().equals(provincia)).mapToDouble(Aemet::getPrecipitation).max().orElse(0.0))
                .findFirst()
                .orElse(new Aemet());

        System.out.println("Precipitacion Maxima de : " + preciMax.getPrecipitation() + " en " + preciMax.getLocalidad());
     logger.debug("Precipitacion Media");
        //Precipitacion Media
        var preciAverage = temps.stream()
                .filter(ae -> ae.getProvincia().equals(provincia))
                .mapToDouble(Aemet::getPrecipitation)
                .average()
                .orElse(0.0);
        System.out.println("Precipitacion Media :" + preciAverage);
    }

    /**
     * Ejecuta una serie de operaciones para analizar y mostrar datos relacionados con el clima.
     * Estas operaciones incluyen:
     * - Busqueda de temperaturas maximas y minimas diarias.
     * - Busqueda de la temperatura maxima por provincia y dia.
     * - Busqueda de la temperatura minima por provincia y dia.
     * - Calculo de la temperatura media por provincia y dia.
     * - Busqueda de la precipitacion maxima por dia y su ubicacion.
     * - Calculo de la precipitacion media por provincia y día.
     * - Identificacion de lugares donde ha llovido por provincia y dia.
     * - Identificacion del lugar con la precipitacion maxima.
     */
    public void ejecutarOperaciones(){

        maxminTempXDay();
        maxTempXProvDay();
        minTempXProvDay();
        averageTempXProvDay();
        pretMaxXDay();
        averagePrecipitationXDay();
        withPrecipitation();
        mostPrecipitation();
    }

}
