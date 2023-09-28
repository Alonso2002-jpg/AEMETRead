package org.develop.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Aemet {
    private Integer id;
    private LocalDate actualDate;
    private String provincia;
    private String localidad;
    private double maxDegrees;
    private double minDegrees;
    private double precipitation;

    private String maxTempHour;
    private String minTempHour;
}
