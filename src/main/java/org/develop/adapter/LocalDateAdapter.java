package org.develop.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * La clase LocalDateAdapter es un adaptador personalizado para la serialización y deserializacion de objetos
 * LocalDate en formato JSON utilizando la biblioteca Gson. Permite convertir objetos LocalDate a formato de cadena
 * JSON y viceversa utilizando el patron "yyyy-MM-dd".
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    /**
     * El formateador de fecha y hora que se utilizara para la conversion de LocalDate a JSON.
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Escribe un objeto LocalDate en formato JSON.
     *
     * @param out    El escritor JSON donde se escribira el valor.
     * @param value  El objeto LocalDate que se va a escribir.
     * @throws IOException Si ocurre un error durante la escritura JSON.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    /**
     * Lee un valor JSON y lo convierte a un objeto LocalDate.
     *
     * @param in El lector JSON que proporciona el valor.
     * @return El objeto LocalDate deserializado.
     * @throws IOException Si ocurre un error durante la lectura JSON.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return null;
    }
}