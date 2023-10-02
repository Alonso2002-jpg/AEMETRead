package org.develop.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * La clase LocalTimeAdapter es un adaptador personalizado para la serializacion y deserializacion de objetos
 * {@code LocalTime} en formato JSON utilizando la biblioteca Gson. Permite convertir objetos LocalTime a formato de cadena
 * JSON y viceversa utilizando el patron "HH:mm".
 */
public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

    /**
     * El formateador de hora que se utilizará para la conversión de LocalTime a JSON.
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Escribe un objeto LocalTime en formato JSON.
     *
     * @param out    El escritor JSON donde se escribira el valor.
     * @param value  El objeto LocalTime que se va a escribir.
     * @throws IOException Si ocurre un error durante la escritura JSON.
     */
    @Override
    public void write(JsonWriter out, LocalTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    /**
     * Lee un valor JSON y lo convierte a un objeto LocalTime.
     *
     * @param in El lector JSON que proporciona el valor.
     * @return El objeto LocalTime deserializado.
     * @throws IOException Si ocurre un error durante la lectura JSON.
     */
    @Override
    public LocalTime read(JsonReader in) throws IOException {
        return null;
    }
}
