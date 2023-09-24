package org.develop.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Day {

    private LocalDate date;
    ArrayList<Provincia> provincias;

    public Day(){
        provincias = new ArrayList<Provincia>();
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ArrayList<Provincia> getProvincias() {
        return provincias;
    }

    public void setProvincias(ArrayList<Provincia> provincias) {
        this.provincias = provincias;
    }

    @Override
    public String toString() {
        return "Day{" +
                "date=" + date +
                ", provincias=" + provincias +
                '}';
    }
}
