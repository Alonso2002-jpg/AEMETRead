package org.develop.repository;

import java.util.List;

public interface AEMETrepository<T,ID> extends CRUDrepository<T,ID> {

    List<T> findByName(String name);
}
