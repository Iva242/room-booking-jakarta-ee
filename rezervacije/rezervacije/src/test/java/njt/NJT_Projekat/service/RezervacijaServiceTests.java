package njt.NJT_Projekat.service;

import java.time.LocalDateTime;
import java.util.Optional;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.dto.impl.RezervacijaDto;
import njt.NJT_Projekat.dto.impl.TipDogadjajaDto;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.entity.enums.StatusRezervacije;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class RezervacijaServiceTests {

    @Autowired
    private RezervacijaService rezervacijaService;

    @MockitoBean
    private RezervacijaRepository rezervacijaRepository;

    @MockitoBean
    private SalaRepository salaRepository;

    @MockitoBean
    private KorisnikRepository korisnikRepository;

    @MockitoBean
    private TipDogadjajaRepository tipDogadjajaRepository;

    private static final Long ID_SALE = 1L;
    private static final Long ID_KORISNIKA = 2L;
    private static final Long ID_TIPA = 3L;
    private static final LocalDateTime POCETAK = LocalDateTime.of(2030, 5, 10, 10, 0);
    private static final LocalDateTime KRAJ = LocalDateTime.of(2030, 5, 10, 12, 0);

    private Sala sala;
    private Korisnik korisnik;
    private TipDogadjaja tipDogadjaja;

    @BeforeEach
    public void pripremiPodatke() throws Exception {
        sala = new Sala();
        sala.setIdSale(ID_SALE);
        sala.setNaziv("Plavi salon");

        korisnik = new Korisnik();
        korisnik.setIdKorisnika(ID_KORISNIKA);
        korisnik.setKorisnickoIme("ana");
        korisnik.setUloga("ROLE_KORISNIK");

        tipDogadjaja = new TipDogadjaja();
        tipDogadjaja.setIdTipDogadjaja(ID_TIPA);
        tipDogadjaja.setNaziv("Sastanak");

        Mockito.when(salaRepository.findById(ID_SALE)).thenReturn(sala);
        Mockito.when(korisnikRepository.findById(ID_KORISNIKA)).thenReturn(korisnik);
        Mockito.when(tipDogadjajaRepository.findById(ID_TIPA)).thenReturn(tipDogadjaja);
    }

    private RezervacijaDto napraviDto() {
        KorisnikDto korisnikDto = new KorisnikDto();
        korisnikDto.setId(ID_KORISNIKA);

        TipDogadjajaDto tipDto = new TipDogadjajaDto();
        tipDto.setIdTipDogadjaja(ID_TIPA);

        RezervacijaDto dto = new RezervacijaDto();
        dto.setIdSale(ID_SALE);
        dto.setKorisnik(korisnikDto);
        dto.setTipDogadjaja(tipDto);
        dto.setPocetak(POCETAK);
        dto.setKraj(KRAJ);
        return dto;
    }

    private Rezervacija napraviRezervaciju(StatusRezervacije status, Korisnik vlasnik) {
        Rezervacija rezervacija = new Rezervacija();
        rezervacija.setIdRezervacije(10L);
        rezervacija.setPocetak(POCETAK);
        rezervacija.setKraj(KRAJ);
        rezervacija.setStatus(status);
        rezervacija.setSala(sala);
        rezervacija.setKorisnik(vlasnik);
        rezervacija.setTipDogadjaja(tipDogadjaja);
        return rezervacija;
    }

    @Test
    public void kreirajRezervacijuTest() throws Exception {
        //Construct
        RezervacijaDto dto = napraviDto();
        Mockito.when(rezervacijaRepository.existsOverlapping(ID_SALE, POCETAK, KRAJ))
                .thenReturn(false);
        Mockito.when(rezervacijaRepository.saveAndReturn(Mockito.any(Rezervacija.class)))
                .thenAnswer(poziv -> poziv.getArgument(0));
        //Act
        RezervacijaDto sacuvana = rezervacijaService.kreirajRezervaciju(dto);
        //Assert
        assertNotNull(sacuvana);
        Assertions.assertEquals(ID_SALE, sacuvana.getIdSale());
        Assertions.assertEquals(StatusRezervacije.NA_CEKANJU, sacuvana.getStatus());
    }

    @Test
    public void kreirajRezervacijuZauzetTerminFailureTest() {
        RezervacijaDto dto = napraviDto();
        Mockito.when(rezervacijaRepository.existsOverlapping(ID_SALE, POCETAK, KRAJ))
                .thenReturn(true);

        Exception greska = Assertions.assertThrows(Exception.class, () -> {
            rezervacijaService.kreirajRezervaciju(dto);
        });
        Assertions.assertEquals("Sala je zauzeta u tom terminu.", greska.getMessage());
    }

    @Test
    public void kreirajRezervacijuZauzetTerminNeCuvaTest() {
        RezervacijaDto dto = napraviDto();
        Mockito.when(rezervacijaRepository.existsOverlapping(ID_SALE, POCETAK, KRAJ))
                .thenReturn(true);

        Assertions.assertThrows(Exception.class, () -> {
            rezervacijaService.kreirajRezervaciju(dto);
        });
        // odbijena rezervacija ne sme da zavrsi u bazi
        Mockito.verify(rezervacijaRepository, Mockito.never())
                .saveAndReturn(Mockito.any(Rezervacija.class));
    }

    @Test
    public void kreirajRezervacijuBezKorisnikaFailureTest() {
        RezervacijaDto dto = napraviDto();
        dto.setKorisnik(null);

        Assertions.assertThrows(Exception.class, () -> {
            rezervacijaService.kreirajRezervaciju(dto);
        });
    }

    @Test
    public void otkaziRezervacijuTest() throws Exception {
        Rezervacija rezervacija = napraviRezervaciju(StatusRezervacije.POTVRDJENA, korisnik);
        Mockito.when(rezervacijaRepository.findById(10L)).thenReturn(rezervacija);
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana"))
                .thenReturn(Optional.of(korisnik));

        rezervacijaService.otkaziRezervaciju(10L, "ana");

        ArgumentCaptor<Rezervacija> uhvacena = ArgumentCaptor.forClass(Rezervacija.class);
        Mockito.verify(rezervacijaRepository, Mockito.times(1)).save(uhvacena.capture());
        Assertions.assertEquals(StatusRezervacije.OTKAZANA, uhvacena.getValue().getStatus());
    }

    @Test
    public void otkaziTudjuRezervacijuFailureTest() throws Exception {
        Korisnik drugi = new Korisnik();
        drugi.setIdKorisnika(99L);
        drugi.setKorisnickoIme("nikola");
        drugi.setUloga("ROLE_KORISNIK");

        Rezervacija rezervacija = napraviRezervaciju(StatusRezervacije.POTVRDJENA, korisnik);
        Mockito.when(rezervacijaRepository.findById(10L)).thenReturn(rezervacija);
        Mockito.when(korisnikRepository.findByKorisnickoIme("nikola"))
                .thenReturn(Optional.of(drugi));

        Assertions.assertThrows(Exception.class, () -> {
            rezervacijaService.otkaziRezervaciju(10L, "nikola");
        });
        Mockito.verify(rezervacijaRepository, Mockito.never()).save(Mockito.any(Rezervacija.class));
    }

    @Test
    public void adminOtkazujeTudjuRezervacijuTest() throws Exception {
        Korisnik admin = new Korisnik();
        admin.setIdKorisnika(1L);
        admin.setKorisnickoIme("iva");
        admin.setUloga("ROLE_ADMIN");

        Rezervacija rezervacija = napraviRezervaciju(StatusRezervacije.POTVRDJENA, korisnik);
        Mockito.when(rezervacijaRepository.findById(10L)).thenReturn(rezervacija);
        Mockito.when(korisnikRepository.findByKorisnickoIme("iva"))
                .thenReturn(Optional.of(admin));

        rezervacijaService.otkaziRezervaciju(10L, "iva");

        Mockito.verify(rezervacijaRepository, Mockito.times(1)).save(Mockito.any(Rezervacija.class));
        Assertions.assertEquals(StatusRezervacije.OTKAZANA, rezervacija.getStatus());
    }

    @Test
    public void potvrdiRezervacijuTest() throws Exception {
        Rezervacija rezervacija = napraviRezervaciju(StatusRezervacije.NA_CEKANJU, korisnik);
        Mockito.when(rezervacijaRepository.findById(10L)).thenReturn(rezervacija);

        rezervacijaService.potvrdiRezervaciju(10L);

        ArgumentCaptor<Rezervacija> uhvacena = ArgumentCaptor.forClass(Rezervacija.class);
        Mockito.verify(rezervacijaRepository, Mockito.times(1)).save(uhvacena.capture());
        Assertions.assertEquals(StatusRezervacije.POTVRDJENA, uhvacena.getValue().getStatus());
    }
}
