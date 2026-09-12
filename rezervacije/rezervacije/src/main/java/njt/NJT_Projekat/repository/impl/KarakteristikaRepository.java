package njt.NJT_Projekat.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Karakteristika;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.repository.MyAppRepository;
import org.springframework.stereotype.Repository;


@Repository
public class KarakteristikaRepository implements MyAppRepository<Karakteristika, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Karakteristika> findAll() {
        return em.createQuery("SELECT k FROM Karakteristika k", Karakteristika.class).getResultList();
    }

    @Override
    public Karakteristika findById(Long id) {
        Karakteristika k = em.find(Karakteristika.class, id);
        if (k == null) throw new EntitetNijePronadjenException("Karakteristika", id);
        return k;
    }

    @Transactional
    @Override
    public void save(Karakteristika entity) {
        if (entity.getIdKarakteristike() == null) em.persist(entity);
        else em.merge(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Karakteristika k = em.find(Karakteristika.class, id);
        if (k != null) em.remove(k);
    }
}