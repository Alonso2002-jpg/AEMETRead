package org.develop.model;

import lombok.Getter;

import java.util.ArrayList;

public class Localidad {
    @Getter
    private String name;

    private Temperatura maxTemperature;

    private Temperatura minTemperature;

    @Getter
    private double precipitation;


    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Temperatura getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Temperatura maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Temperatura getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Temperatura minTemperature) {
        this.minTemperature = minTemperature;
    }

    @Override
    public String toString() {
        return "Localidad{" +
                "name='" + name + '\'' +
                ", maxTemperature=" + maxTemperature +
                ", minTemperature=" + minTemperature +
                ", precipitation=" + precipitation +
                '}';
    }
}
