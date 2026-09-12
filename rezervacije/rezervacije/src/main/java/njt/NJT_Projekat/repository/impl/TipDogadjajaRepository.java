package njt.NJT_Projekat.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import njt.NJT_Projekat.entity.impl.TipDogadjaja;
import njt.NJT_Projekat.repository.MyAppRepository;
import org.springframework.stereotype.Repository;


@Repository
public class TipDogadjajaRepository implements MyAppRepository<TipDogadjaja, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<TipDogadjaja> findAll() {
        return em.createQuery("SELECT t FROM TipDogadjaja t", TipDogadjaja.class).getResultList();
    }

    @Override
    public TipDogadjaja findById(Long id) throws Exception {
        TipDogadjaja t = em.find(TipDogadjaja.class, id);
        if (t == null) throw new Exception("Tip događaja sa ID-jem " + id + " nije pronađen.");
        return t;
    }

    @Transactional
    @Override
    public void save(TipDogadjaja entity) {
        if (entity.getIdTipDogadjaja() == null) em.persist(entity);
        else em.merge(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        TipDogadjaja t = em.find(TipDogadjaja.class, id);
        if (t != null) em.remove(t);
    }
}