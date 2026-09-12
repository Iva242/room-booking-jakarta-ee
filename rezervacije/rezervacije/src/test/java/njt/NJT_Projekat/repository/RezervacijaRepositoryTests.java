package njt.NJT_Projekat.repository;

import java.time.LocalDateTime;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.entity.impl.TipDogadjaja;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import njt.NJT_Projekat.repository.impl.RezervacijaRepository;
import njt.NJT_Projekat.repository.impl.SalaRepository;
import njt.NJT_Projekat.repository.impl.TipDogadjajaRepository;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RezervacijaRepositoryTests {

    @Autowired
    private RezervacijaRepository rezervacijaRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private KorisnikRepository korisnikRepository;

    @Autowired
    private TipDogadjajaRepository tipDogadjajaRepository;

    private Sala sala;
    private Korisnik korisnik;
    private TipDogadjaja tipDogadjaja;

    private static final LocalDateTime POCETAK = LocalDateTime.of(2030, 5, 10, 10, 0);
    private static final LocalDateTime KRAJ = LocalDateTime.of(2030, 5, 10, 12, 0);

    @BeforeEach
    public void pripremiPodatke() {
        sala = new Sala();
        sala.setNaziv("Sala za test");
        sala.setKapacitet(20);
        sala.setLokacija("Beograd");
        salaRepository.save(sala);

        korisnik = new Korisnik();
        korisnik.setIme("Test");
        korisnik.setPrezime("Testic");
        korisnik.setKorisnickoIme("test" + System.nanoTime());
        korisnik.setLozinka("tajna");
        korisnik.setEmail("test@primer.rs");
        korisnik.setUloga("ROLE_KORISNIK");
        korisnik.setAktivan(true);
        korisnikRepository.save(korisnik);

        tipDogadjaja = new TipDogadjaja();
        tipDogadjaja.setNaziv("Sastanak");
        tipDogadjajaRepository.save(tipDogadjaja);
    }

    private Rezervacija sacuvajRezervaciju(LocalDateTime pocetak, LocalDateTime kraj, String status) {
        Rezervacija rezervacija = new Rezervacija();
        rezervacija.setPocetak(pocetak);
        rezervacija.setKraj(kraj);
        rezervacija.setStatus(status);
        rezervacija.setSala(sala);
        rezervacija.setKorisnik(korisnik);
        rezervacija.setTipDogadjaja(tipDogadjaja);
        return rezervacijaRepository.saveAndReturn(rezervacija);
    }

    @Test
    public void saveTest() {
        Rezervacija rezervacija = sacuvajRezervaciju(POCETAK, KRAJ, "na_cekanju");
        assertNotNull(rezervacija);
        assertNotNull(rezervacija.getIdRezervacije());
    }

    @Test
    public void findBySalaIdTest() {
        sacuvajRezervaciju(POCETAK, KRAJ, "na_cekanju");
        List<Rezervacija> rezervacije = rezervacijaRepository.findBySalaId(sala.getIdSale());
        Assertions.assertEquals(1, rezervacije.size());
        Assertions.assertEquals(sala.getIdSale(), rezervacije.get(0).getSala().getIdSale());
    }

    @Test
    public void findByKorisnikIdKorisnikaTest() {
        sacuvajRezervaciju(POCETAK, KRAJ, "na_cekanju");
        List<Rezervacija> rezervacije =
                rezervacijaRepository.findByKorisnikIdKorisnika(korisnik.getIdKorisnika());
        Assertions.assertEquals(1, rezervacije.size());
    }

    @Test
    public void existsOverlappingPreklapanjeTest() {
        sacuvajRezervaciju(POCETAK, KRAJ, "potvrdjena");
        // trazeni termin pocinje u 11, dok prethodna rezervacija traje do 12
        boolean preklapa = rezervacijaRepository.existsOverlapping(
                sala.getIdSale(),
                LocalDateTime.of(2030, 5, 10, 11, 0),
                LocalDateTime.of(2030, 5, 10, 13, 0));
        Assertions.assertTrue(preklapa);
    }

    @Test
    public void existsOverlappingSlobodanTerminTest() {
        sacuvajRezervaciju(POCETAK, KRAJ, "potvrdjena");
        // trazeni termin pocinje tacno kada prethodni zavrsava
        boolean preklapa = rezervacijaRepository.existsOverlapping(
                sala.getIdSale(),
                LocalDateTime.of(2030, 5, 10, 12, 0),
                LocalDateTime.of(2030, 5, 10, 14, 0));
        Assertions.assertFalse(preklapa);
    }

    @Test
    public void existsOverlappingOtkazanaNeBlokiraTest() {
        sacuvajRezervaciju(POCETAK, KRAJ, "otkazana");
        // otkazana rezervacija ne sme da drzi salu zauzetom
        boolean preklapa = rezervacijaRepository.existsOverlapping(
                sala.getIdSale(), POCETAK, KRAJ);
        Assertions.assertFalse(preklapa);
    }
}
