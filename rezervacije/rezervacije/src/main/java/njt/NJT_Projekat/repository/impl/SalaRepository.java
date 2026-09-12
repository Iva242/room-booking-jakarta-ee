package njt.NJT_Projekat.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.repository.MyAppRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SalaRepository implements MyAppRepository<Sala, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Sala> findAll() {

        return em.createQuery("SELECT s FROM Sala s", Sala.class)
                .getResultList();
    }

    @Override
    public Sala findById(Long id) throws Exception {
        Sala s = em.find(Sala.class, id);
        if (s == null) {
            throw new Exception("Sala sa ID-jem " + id + " nije pronađena.");
        }
        return s;
    }

    @Transactional
    @Override
    public void save(Sala entity) {

        if (entity.getIdSale() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Sala s = em.find(Sala.class, id);
        if (s != null) {
            em.remove(s);
        }
    }

    public List<Sala> findByNazivContainingIgnoreCaseOrLokacijaContainingIgnoreCase(String naziv, String lokacija) {
        String jpql = "SELECT s FROM Sala s WHERE LOWER(s.naziv) LIKE LOWER(:naziv) OR LOWER(s.lokacija) LIKE LOWER(:lokacija)";
        return em.createQuery(jpql, Sala.class)
                .setParameter("naziv", "%" + naziv + "%")
                .setParameter("lokacija", "%" + lokacija + "%")
                .getResultList();
    }
}
