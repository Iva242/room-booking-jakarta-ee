package njt.NJT_Projekat.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Poruka;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.repository.MyAppRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PorukaRepository implements MyAppRepository<Poruka, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Poruka> findAll() {
        return em.createQuery("SELECT p FROM Poruka p ORDER BY p.vreme", Poruka.class)
                .getResultList();
    }

    @Override
    public Poruka findById(Long id) {
        Poruka p = em.find(Poruka.class, id);
        if (p == null) {
            throw new EntitetNijePronadjenException("Poruka", id);
        }
        return p;
    }

    @Transactional
    @Override
    public void save(Poruka entity) {
        if (entity.getIdPoruke() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    public Poruka saveAndReturn(Poruka entity) {
        return em.merge(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Poruka p = em.find(Poruka.class, id);
        if (p != null) {
            em.remove(p);
        }
    }

    public List<Poruka> findByKonverzacija(Long idKonverzacije) {
        return em.createQuery(
                "SELECT p FROM Poruka p WHERE p.konverzacija.idKonverzacije = :id ORDER BY p.vreme",
                Poruka.class)
                .setParameter("id", idKonverzacije)
                .getResultList();
    }

    // nepročitane poruke u jednom razgovoru koje nije napisao onaj ko gleda
    public long brojNeprocitanihUKonverzaciji(Long idKonverzacije, Long idCitaoca) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Poruka p WHERE p.konverzacija.idKonverzacije = :id "
                + "AND p.autor.idKorisnika <> :citalac AND p.procitana = false", Long.class)
                .setParameter("id", idKonverzacije)
                .setParameter("citalac", idCitaoca)
                .getSingleResult();
    }

    // ukupno nepročitanih za korisnika, u svim njegovim razgovorima
    public long brojNeprocitanihZaKorisnika(Long idKorisnika) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Poruka p WHERE p.konverzacija.korisnik.idKorisnika = :id "
                + "AND p.autor.idKorisnika <> :id AND p.procitana = false", Long.class)
                .setParameter("id", idKorisnika)
                .getSingleResult();
    }

    // ukupno nepročitanih za administratora, u svim razgovorima
    public long brojNeprocitanihZaAdmina(Long idAdmina) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Poruka p WHERE p.autor.idKorisnika <> :id "
                + "AND p.procitana = false", Long.class)
                .setParameter("id", idAdmina)
                .getSingleResult();
    }

    // poruke se ucitavaju pa im se menja polje, umesto masovnog UPDATE upita.
    // masovni UPDATE ide pravo u bazu i zaobilazi vec ucitane objekte, pa bi
    // isti zahtev odmah zatim vratio poruke sa starom vrednoscu.
    @Transactional
    public void oznaciProcitane(Long idKonverzacije, Long idCitaoca) {
        List<Poruka> neprocitane = em.createQuery(
                "SELECT p FROM Poruka p WHERE p.konverzacija.idKonverzacije = :id "
                + "AND p.autor.idKorisnika <> :citalac AND p.procitana = false", Poruka.class)
                .setParameter("id", idKonverzacije)
                .setParameter("citalac", idCitaoca)
                .getResultList();

        for (Poruka poruka : neprocitane) {
            poruka.setProcitana(true);
            em.merge(poruka);
        }
    }
}
