package org.develop.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Provincia  {
    private String name;
    ArrayList<Localidad> localities = new ArrayList<Localidad>();

}
