package njt.NJT_Projekat.repository;

import java.util.List;


public interface MyAppRepository<E, ID> {
    List<E> findAll();
    E findById(ID id) throws Exception;
    void save(E entity);
    void deleteById(ID id);
}