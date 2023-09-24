package org.develop.model;

import java.time.LocalDateTime;

public class Temperatura {
    private double degrees;
    private String hour;

    public Temperatura() {
    }

    public double getDegrees() {
        return degrees;
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

}
