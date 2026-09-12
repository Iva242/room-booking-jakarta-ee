package njt.NJT_Projekat.repository;

import java.util.List;


public interface MyAppRepository<E, ID> {
    List<E> findAll();
    E findById(ID id);
    void save(E entity);
    void deleteById(ID id);
}