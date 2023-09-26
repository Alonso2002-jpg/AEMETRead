package org.develop.model;

import lombok.Data;

@Data
public class Localidad {
    private String name;

    private Temperatura maxTemperature;

    private Temperatura minTemperature;

    private double precipitation;

}
