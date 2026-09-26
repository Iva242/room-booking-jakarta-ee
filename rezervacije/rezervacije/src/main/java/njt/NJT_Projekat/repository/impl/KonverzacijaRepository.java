package njt.NJT_Projekat.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Konverzacija;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.repository.MyAppRepository;
import org.springframework.stereotype.Repository;

@Repository
public class KonverzacijaRepository implements MyAppRepository<Konverzacija, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Konverzacija> findAll() {
        return em.createQuery(
                "SELECT k FROM Konverzacija k ORDER BY k.poslednjaAktivnost DESC",
                Konverzacija.class).getResultList();
    }

    @Override
    public Konverzacija findById(Long id) {
        Konverzacija k = em.find(Konverzacija.class, id);
        if (k == null) {
            throw new EntitetNijePronadjenException("Konverzacija", id);
        }
        return k;
    }

    @Transactional
    @Override
    public void save(Konverzacija entity) {
        if (entity.getIdKonverzacije() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    public Konverzacija saveAndReturn(Konverzacija entity) {
        return em.merge(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Konverzacija k = em.find(Konverzacija.class, id);
        if (k != null) {
            em.remove(k);
        }
    }

    // razgovori o jednoj sali; opsta pitanja nemaju salu pa ovde ne ulaze
    public List<Konverzacija> findBySala(Long idSale) {
        return em.createQuery(
                "SELECT k FROM Konverzacija k WHERE k.sala.idSale = :idSale", Konverzacija.class)
                .setParameter("idSale", idSale)
                .getResultList();
    }

    public List<Konverzacija> findByKorisnik(Long idKorisnika) {
        return em.createQuery(
                "SELECT k FROM Konverzacija k WHERE k.korisnik.idKorisnika = :idKorisnika "
                + "ORDER BY k.poslednjaAktivnost DESC", Konverzacija.class)
                .setParameter("idKorisnika", idKorisnika)
                .getResultList();
    }
}
