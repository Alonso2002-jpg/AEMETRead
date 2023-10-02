package org.develop.repository;

import java.util.List;

/**
 * La interfaz AEMETrepository proporciona operaciones de repositorio genericas para acceder y manipular datos relacionados con (AEMET).
 * Esta interfaz extiende la interfaz CRUDrepository y agrega una operacion especifica para buscar objetos por nombre de localidad.
 * @param <T>  El tipo de entidad que representa el repositorio.
 * @param <ID> El tipo de identificador unico utilizado para las entidades.
 */
public interface AEMETrepository<T,ID> extends CRUDrepository<T,ID> {

    /**
     * Busca una lista de objetos por nombre de localidad.
     * @param name El nombre de la localidad por el cual se va a realizar la busqueda.
     */
    List<T> findByLocalidad(String name);
}
