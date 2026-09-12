package njt.NJT_Projekat.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.repository.MyAppRepository;
import org.springframework.stereotype.Repository;


@Repository
public class RezervacijaRepository implements MyAppRepository<Rezervacija, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Rezervacija> findAll() {
        return em.createQuery("SELECT r FROM Rezervacija r", Rezervacija.class).getResultList();
    }

    @Override
    public Rezervacija findById(Long id) throws Exception {
        Rezervacija r = em.find(Rezervacija.class, id);
        if (r == null) throw new Exception("Rezervacija sa ID-jem " + id + " nije pronađena.");
        return r;
    }

@Transactional
    @Override
    public void save(Rezervacija entity) {
        if (entity.getIdRezervacije() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    // specijalna metoda samo za rezervacije
    @Transactional
    public Rezervacija saveAndReturn(Rezervacija entity) {
        return em.merge(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Rezervacija r = em.find(Rezervacija.class, id);
        if (r != null) {
            em.remove(r);
        }
    }

    public List<Rezervacija> findByKorisnikIdKorisnika(Long idKorisnika) {
        String jpql = "SELECT r FROM Rezervacija r WHERE r.korisnik.idKorisnika = :idKorisnika";
        return em.createQuery(jpql, Rezervacija.class)
                .setParameter("idKorisnika", idKorisnika)
                .getResultList();
    }
    
    public boolean existsOverlapping(Long salaId, LocalDateTime pocetak, LocalDateTime kraj) {
    // JPQL query koji trazi bilo koju rezervaciju u istoj sali koja se preklapa
    // ignorismemo otkazane i zavrsene jer one ne blokiraju salu
    String jpql = "SELECT COUNT(r) FROM Rezervacija r " +
                  "WHERE r.sala.idSale = :salaId " +
                  "AND r.status NOT IN ('OTKAZANA', 'ZAVRSENA') " +
                  "AND r.pocetak < :kraj AND r.kraj > :pocetak";

    Long count = em.createQuery(jpql, Long.class)
            .setParameter("salaId", salaId)
            .setParameter("pocetak", pocetak)
            .setParameter("kraj", kraj)
            .getSingleResult();

    return count > 0; // Ako je count > 0, znači da postoji preklapanje!
}
    
    public List<Rezervacija> findBySalaId(Long salaId) {
    String jpql = "SELECT r FROM Rezervacija r WHERE r.sala.idSale = :salaId";
    return em.createQuery(jpql, Rezervacija.class)
            .setParameter("salaId", salaId)
            .getResultList();
}
    
    @Transactional
public void deleteAll(List<Rezervacija> rezervacije) {
    for (Rezervacija r : rezervacije) {
        // "em.merge(r)" osigurava da je objekat "managed" (pod nadzorom) pre brisanja
        em.remove(em.contains(r) ? r : em.merge(r));
    }
}
}