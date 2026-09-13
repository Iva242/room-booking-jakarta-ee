package njt.NJT_Projekat.repository;

import java.time.LocalDateTime;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Konverzacija;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.entity.impl.Poruka;
import njt.NJT_Projekat.repository.impl.KonverzacijaRepository;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import njt.NJT_Projekat.repository.impl.PorukaRepository;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PorukaRepositoryTests {

    @Autowired
    private PorukaRepository porukaRepository;

    @Autowired
    private KonverzacijaRepository konverzacijaRepository;

    @Autowired
    private KorisnikRepository korisnikRepository;

    private Korisnik korisnik;
    private Korisnik admin;
    private Konverzacija konverzacija;

    private Korisnik sacuvajKorisnika(String korisnickoIme, String uloga) {
        Korisnik k = new Korisnik();
        k.setIme("Test");
        k.setPrezime("Testic");
        k.setKorisnickoIme(korisnickoIme + System.nanoTime());
        k.setLozinka("hesirana");
        k.setEmail(korisnickoIme + "@primer.rs");
        k.setUloga(uloga);
        k.setAktivan(true);
        korisnikRepository.save(k);
        return k;
    }

    private Poruka posalji(Korisnik autor, String tekst) {
        Poruka p = new Poruka();
        p.setKonverzacija(konverzacija);
        p.setAutor(autor);
        p.setTekst(tekst);
        p.setVreme(LocalDateTime.now());
        p.setProcitana(false);
        return porukaRepository.saveAndReturn(p);
    }

    @BeforeEach
    public void pripremiPodatke() {
        korisnik = sacuvajKorisnika("kor", "ROLE_KORISNIK");
        admin = sacuvajKorisnika("adm", "ROLE_ADMIN");

        konverzacija = new Konverzacija();
        konverzacija.setKorisnik(korisnik);
        konverzacija.setKreirana(LocalDateTime.now());
        konverzacija.setPoslednjaAktivnost(LocalDateTime.now());
        konverzacija = konverzacijaRepository.saveAndReturn(konverzacija);
    }

    @Test
    public void saveTest() {
        Poruka p = posalji(korisnik, "Koja je cena?");
        assertNotNull(p);
        assertNotNull(p.getIdPoruke());
    }

    @Test
    public void findByKonverzacijaTest() {
        posalji(korisnik, "Prva");
        posalji(admin, "Druga");

        List<Poruka> poruke = porukaRepository.findByKonverzacija(konverzacija.getIdKonverzacije());

        Assertions.assertEquals(2, poruke.size());
        // poruke se vracaju hronoloski
        Assertions.assertEquals("Prva", poruke.get(0).getTekst());
        Assertions.assertEquals("Druga", poruke.get(1).getTekst());
    }

    @Test
    public void autorNeVidiSopstvenuPorukuKaoNeprocitanuTest() {
        posalji(korisnik, "Moja poruka");

        long zaKorisnika = porukaRepository.brojNeprocitanihUKonverzaciji(
                konverzacija.getIdKonverzacije(), korisnik.getIdKorisnika());
        long zaAdmina = porukaRepository.brojNeprocitanihUKonverzaciji(
                konverzacija.getIdKonverzacije(), admin.getIdKorisnika());

        Assertions.assertEquals(0, zaKorisnika);
        Assertions.assertEquals(1, zaAdmina);
    }

    @Test
    public void oznaciProcitaneTest() {
        posalji(korisnik, "Pitanje");
        posalji(korisnik, "Jos jedno pitanje");

        porukaRepository.oznaciProcitane(konverzacija.getIdKonverzacije(), admin.getIdKorisnika());

        long ostalo = porukaRepository.brojNeprocitanihUKonverzaciji(
                konverzacija.getIdKonverzacije(), admin.getIdKorisnika());
        Assertions.assertEquals(0, ostalo);
    }

    @Test
    public void oznaciProcitaneNeDiraSopstvenePorukeTest() {
        posalji(korisnik, "Pitanje korisnika");
        posalji(admin, "Odgovor administratora");

        // korisnik otvara razgovor — sme da se oznaci samo administratorova poruka
        porukaRepository.oznaciProcitane(konverzacija.getIdKonverzacije(), korisnik.getIdKorisnika());

        List<Poruka> poruke = porukaRepository.findByKonverzacija(konverzacija.getIdKonverzacije());
        Assertions.assertFalse(poruke.get(0).isProcitana());
        Assertions.assertTrue(poruke.get(1).isProcitana());
    }

    @Test
    public void brojNeprocitanihZaKorisnikaTest() {
        posalji(admin, "Odgovor jedan");
        posalji(admin, "Odgovor dva");
        posalji(korisnik, "Moja poruka");

        long broj = porukaRepository.brojNeprocitanihZaKorisnika(korisnik.getIdKorisnika());

        // racunaju se samo tudje poruke
        Assertions.assertEquals(2, broj);
    }

    @Test
    public void brojNeprocitanihZaAdminaTest() {
        posalji(korisnik, "Pitanje");
        posalji(admin, "Odgovor");

        long broj = porukaRepository.brojNeprocitanihZaAdmina(admin.getIdKorisnika());

        Assertions.assertEquals(1, broj);
    }
}
