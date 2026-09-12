package njt.NJT_Projekat.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.repository.MyAppRepository;
import org.springframework.stereotype.Repository;


@Repository
public class KorisnikRepository implements MyAppRepository<Korisnik, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Korisnik> findAll() {
        return em.createQuery("SELECT k FROM Korisnik k", Korisnik.class).getResultList();
    }

    @Override
    public Korisnik findById(Long id) throws Exception {
        Korisnik k = em.find(Korisnik.class, id);
        if (k == null) throw new Exception("Korisnik sa ID-jem " + id + " nije pronađen.");
        return k;
    }

    @Transactional
    @Override
    public void save(Korisnik entity) {
        // Koristimo getIdKorisnika() jer smo videli da ti se tako zove polje u bazi
        if (entity.getIdKorisnika() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Korisnik k = em.find(Korisnik.class, id);
        if (k != null) {
            em.remove(k);
        }
    }

    public Optional<Korisnik> findByKorisnickoIme(String korisnickoIme) {
        List<Korisnik> list = em.createQuery("SELECT k FROM Korisnik k WHERE k.korisnickoIme = :un", Korisnik.class)
                .setParameter("un", korisnickoIme)
                .getResultList();
        return list.stream().findFirst();
    }

    public List<Korisnik> findByImeContainingIgnoreCaseOrPrezimeContainingIgnoreCase(String ime, String prezime) {
        String jpql = "SELECT k FROM Korisnik k WHERE LOWER(k.ime) LIKE LOWER(:ime) OR LOWER(k.prezime) LIKE LOWER(:prezime)";
        return em.createQuery(jpql, Korisnik.class)
                .setParameter("ime", "%" + ime + "%")
                .setParameter("prezime", "%" + prezime + "%")
                .getResultList();
    }

    
    public List<Korisnik> findByUloga(String uloga) {
    return em.createQuery("SELECT k FROM Korisnik k WHERE k.uloga = :uloga", Korisnik.class)
            .setParameter("uloga", uloga)
            .getResultList();
}
}