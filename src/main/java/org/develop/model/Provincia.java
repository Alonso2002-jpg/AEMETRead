package org.develop.model;

import java.util.ArrayList;

public class Provincia  {
    private String name;
    ArrayList<Localidad> localities;

    public Provincia(){
        localities=new ArrayList<Localidad>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Localidad> getLocalities() {
        return localities;
    }

    public void setLocalities(ArrayList<Localidad> localities) {
        this.localities = localities;
    }

    @Override
    public String toString() {
        return "Provincia{" +
                "name='" + name + '\'' +
                ", localities=" + localities +
                '}';
    }
}
