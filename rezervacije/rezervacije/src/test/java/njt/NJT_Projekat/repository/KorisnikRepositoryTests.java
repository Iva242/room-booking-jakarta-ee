package njt.NJT_Projekat.repository;

import java.util.List;
import java.util.Optional;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class KorisnikRepositoryTests {

    @Autowired
    private KorisnikRepository korisnikRepository;

    private Korisnik sacuvajKorisnika(String ime, String prezime, String korisnickoIme, String uloga) {
        Korisnik korisnik = new Korisnik();
        korisnik.setIme(ime);
        korisnik.setPrezime(prezime);
        korisnik.setKorisnickoIme(korisnickoIme);
        korisnik.setLozinka("hesirana");
        korisnik.setEmail(korisnickoIme + "@primer.rs");
        korisnik.setUloga(uloga);
        korisnik.setAktivan(true);
        korisnikRepository.save(korisnik);
        return korisnik;
    }

    @BeforeEach
    public void pripremiPodatke() {
        sacuvajKorisnika("Iva", "Peric", "iva_test", "ROLE_ADMIN");
        sacuvajKorisnika("Ana", "Peric", "ana_test", "ROLE_KORISNIK");
        sacuvajKorisnika("Nikola", "Mitic", "nikola_test", "ROLE_KORISNIK");
    }

    @Test
    public void saveTest() {
        Korisnik korisnik = sacuvajKorisnika("Jovan", "Jovic", "joca_test", "ROLE_KORISNIK");
        assertNotNull(korisnik);
        assertNotNull(korisnik.getIdKorisnika());
    }

    @Test
    public void findByKorisnickoImeTest() {
        Optional<Korisnik> pronadjen = korisnikRepository.findByKorisnickoIme("ana_test");

        Assertions.assertTrue(pronadjen.isPresent());
        Assertions.assertEquals("Ana", pronadjen.get().getIme());
    }

    @Test
    public void findByKorisnickoImeNepostojeciTest() {
        Optional<Korisnik> pronadjen = korisnikRepository.findByKorisnickoIme("nepostoji");

        Assertions.assertTrue(pronadjen.isEmpty());
    }

    @Test
    public void findByUlogaTest() {
        List<Korisnik> korisnici = korisnikRepository.findByUloga("ROLE_KORISNIK");

        Assertions.assertTrue(korisnici.size() >= 2);
        // u rezultatu ne sme da se nadje nijedan administrator
        Assertions.assertTrue(korisnici.stream()
                .allMatch(k -> "ROLE_KORISNIK".equals(k.getUloga())));
    }

    @Test
    public void findByUlogaAdminTest() {
        List<Korisnik> administratori = korisnikRepository.findByUloga("ROLE_ADMIN");

        Assertions.assertTrue(administratori.stream()
                .anyMatch(k -> "iva_test".equals(k.getKorisnickoIme())));
    }

    @Test
    public void pretragaPoImenuTest() {
        List<Korisnik> rezultat = korisnikRepository
                .findByImeContainingIgnoreCaseOrPrezimeContainingIgnoreCase("nikola", "nikola");

        Assertions.assertEquals(1, rezultat.size());
        Assertions.assertEquals("nikola_test", rezultat.get(0).getKorisnickoIme());
    }

    @Test
    public void pretragaPoPrezimenuTest() {
        List<Korisnik> rezultat = korisnikRepository
                .findByImeContainingIgnoreCaseOrPrezimeContainingIgnoreCase("Peric", "Peric");

        // prezime Peric imaju i Iva i Ana
        Assertions.assertEquals(2, rezultat.size());
    }

    @Test
    public void pretragaBezRezultataTest() {
        List<Korisnik> rezultat = korisnikRepository
                .findByImeContainingIgnoreCaseOrPrezimeContainingIgnoreCase("xyz", "xyz");

        Assertions.assertTrue(rezultat.isEmpty());
    }

    @Test
    public void deleteByIdTest() {
        Korisnik korisnik = sacuvajKorisnika("Za", "Brisanje", "brisanje_test", "ROLE_KORISNIK");
        Long id = korisnik.getIdKorisnika();

        korisnikRepository.deleteById(id);

        Assertions.assertTrue(korisnikRepository.findByKorisnickoIme("brisanje_test").isEmpty());
    }
}
