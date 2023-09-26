package org.develop.model;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class Day {

    private LocalDate date;
    ArrayList<Provincia> provincias=new ArrayList<Provincia>();

}
