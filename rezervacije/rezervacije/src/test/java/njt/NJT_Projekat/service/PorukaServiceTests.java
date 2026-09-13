package njt.NJT_Projekat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import njt.NJT_Projekat.dto.impl.KonverzacijaDto;
import njt.NJT_Projekat.dto.impl.NovaPorukaDto;
import njt.NJT_Projekat.dto.impl.PorukaDto;
import njt.NJT_Projekat.entity.impl.Konverzacija;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.entity.impl.Poruka;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.exception.NemaDozvoluException;
import njt.NJT_Projekat.exception.NevalidanZahtevException;
import njt.NJT_Projekat.repository.impl.KonverzacijaRepository;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import njt.NJT_Projekat.repository.impl.PorukaRepository;
import njt.NJT_Projekat.repository.impl.SalaRepository;
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
public class PorukaServiceTests {

    @Autowired
    private PorukaService porukaService;

    @MockitoBean
    private KonverzacijaRepository konverzacijaRepository;

    @MockitoBean
    private PorukaRepository porukaRepository;

    @MockitoBean
    private KorisnikRepository korisnikRepository;

    @MockitoBean
    private SalaRepository salaRepository;

    private static final Long ID_KONVERZACIJE = 5L;
    private static final Long ID_SALE = 2L;

    private Korisnik korisnik;
    private Korisnik drugiKorisnik;
    private Korisnik admin;
    private Sala sala;
    private Konverzacija konverzacija;

    private Korisnik napraviKorisnika(Long id, String ime, String uloga) {
        Korisnik k = new Korisnik();
        k.setIdKorisnika(id);
        k.setKorisnickoIme(ime);
        k.setUloga(uloga);
        return k;
    }

    @BeforeEach
    public void pripremiPodatke() {
        korisnik = napraviKorisnika(1L, "ana", "ROLE_KORISNIK");
        drugiKorisnik = napraviKorisnika(2L, "nikola", "ROLE_KORISNIK");
        admin = napraviKorisnika(3L, "iva", "ROLE_ADMIN");

        sala = new Sala();
        sala.setIdSale(ID_SALE);
        sala.setNaziv("Plavi salon");

        konverzacija = new Konverzacija();
        konverzacija.setIdKonverzacije(ID_KONVERZACIJE);
        konverzacija.setKorisnik(korisnik);
        konverzacija.setSala(sala);
        konverzacija.setPoslednjaAktivnost(LocalDateTime.now());

        Mockito.when(korisnikRepository.findByKorisnickoIme("ana")).thenReturn(Optional.of(korisnik));
        Mockito.when(korisnikRepository.findByKorisnickoIme("nikola")).thenReturn(Optional.of(drugiKorisnik));
        Mockito.when(korisnikRepository.findByKorisnickoIme("iva")).thenReturn(Optional.of(admin));
        Mockito.when(salaRepository.findById(ID_SALE)).thenReturn(sala);
        Mockito.when(konverzacijaRepository.findById(ID_KONVERZACIJE)).thenReturn(konverzacija);
        Mockito.when(konverzacijaRepository.saveAndReturn(Mockito.any(Konverzacija.class)))
                .thenAnswer(poziv -> poziv.getArgument(0));
        Mockito.when(porukaRepository.saveAndReturn(Mockito.any(Poruka.class)))
                .thenAnswer(poziv -> poziv.getArgument(0));
    }

    private NovaPorukaDto zahtev(Long idSale, String tekst) {
        NovaPorukaDto z = new NovaPorukaDto();
        z.setIdSale(idSale);
        z.setTekst(tekst);
        return z;
    }

    @Test
    public void zapocniKonverzacijuTest() {
        //Construct
        NovaPorukaDto z = zahtev(ID_SALE, "Koja je cena zakupa?");
        //Act
        KonverzacijaDto dto = porukaService.zapocniKonverzaciju(z, "ana");
        //Assert
        assertNotNull(dto);
        Assertions.assertEquals(ID_SALE, dto.getIdSale());
        Assertions.assertEquals("Plavi salon", dto.getNazivSale());
    }

    @Test
    public void zapocniKonverzacijuBezSaleTest() {
        // opste pitanje, bez vezivanja za salu
        KonverzacijaDto dto = porukaService.zapocniKonverzaciju(zahtev(null, "Radite li vikendom?"), "ana");

        assertNotNull(dto);
        Assertions.assertNull(dto.getIdSale());
        Mockito.verify(salaRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    public void zapocniKonverzacijuCuvaPrvuPorukuTest() {
        porukaService.zapocniKonverzaciju(zahtev(ID_SALE, "  Koja je cena?  "), "ana");

        ArgumentCaptor<Poruka> uhvacena = ArgumentCaptor.forClass(Poruka.class);
        Mockito.verify(porukaRepository).saveAndReturn(uhvacena.capture());

        Assertions.assertEquals("Koja je cena?", uhvacena.getValue().getTekst());
        Assertions.assertEquals(korisnik, uhvacena.getValue().getAutor());
        Assertions.assertFalse(uhvacena.getValue().isProcitana());
    }

    @Test
    public void adminNeMozeDaZapocneKonverzacijuFailureTest() {
        Assertions.assertThrows(NemaDozvoluException.class, () -> {
            porukaService.zapocniKonverzaciju(zahtev(ID_SALE, "zdravo"), "iva");
        });
        Mockito.verify(konverzacijaRepository, Mockito.never())
                .saveAndReturn(Mockito.any(Konverzacija.class));
    }

    @Test
    public void praznaPorukaFailureTest() {
        Assertions.assertThrows(NevalidanZahtevException.class, () -> {
            porukaService.zapocniKonverzaciju(zahtev(ID_SALE, "   "), "ana");
        });
    }

    @Test
    public void posaljiPorukuKaoVlasnikTest() {
        PorukaDto dto = porukaService.posaljiPoruku(ID_KONVERZACIJE, zahtev(null, "Hvala"), "ana");

        assertNotNull(dto);
        Assertions.assertEquals("Hvala", dto.getTekst());
    }

    @Test
    public void posaljiPorukuKaoAdminTest() {
        // administrator sme da odgovara u tudjem razgovoru
        PorukaDto dto = porukaService.posaljiPoruku(ID_KONVERZACIJE, zahtev(null, "1400 dinara"), "iva");

        assertNotNull(dto);
        Assertions.assertEquals("iva", dto.getAutor().getKorisnickoIme());
    }

    @Test
    public void posaljiPorukuUTudjRazgovorFailureTest() {
        Assertions.assertThrows(NemaDozvoluException.class, () -> {
            porukaService.posaljiPoruku(ID_KONVERZACIJE, zahtev(null, "upad"), "nikola");
        });
        Mockito.verify(porukaRepository, Mockito.never()).saveAndReturn(Mockito.any(Poruka.class));
    }

    @Test
    public void porukeTudjegRazgovoraFailureTest() {
        Assertions.assertThrows(NemaDozvoluException.class, () -> {
            porukaService.poruke(ID_KONVERZACIJE, "nikola");
        });
        Mockito.verify(porukaRepository, Mockito.never())
                .oznaciProcitane(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void otvaranjeRazgovoraOznacavaProcitaneTest() {
        Mockito.when(porukaRepository.findByKonverzacija(ID_KONVERZACIJE)).thenReturn(List.of());

        porukaService.poruke(ID_KONVERZACIJE, "ana");

        Mockito.verify(porukaRepository, Mockito.times(1))
                .oznaciProcitane(ID_KONVERZACIJE, korisnik.getIdKorisnika());
    }

    @Test
    public void korisnikVidiSamoSvojeKonverzacijeTest() {
        Mockito.when(konverzacijaRepository.findByKorisnik(korisnik.getIdKorisnika()))
                .thenReturn(List.of(konverzacija));
        Mockito.when(porukaRepository.findByKonverzacija(ID_KONVERZACIJE)).thenReturn(List.of());

        List<KonverzacijaDto> lista = porukaService.konverzacije("ana");

        Assertions.assertEquals(1, lista.size());
        Mockito.verify(konverzacijaRepository, Mockito.never()).findAll();
    }

    @Test
    public void adminVidiSveKonverzacijeTest() {
        Mockito.when(konverzacijaRepository.findAll()).thenReturn(List.of(konverzacija));
        Mockito.when(porukaRepository.findByKonverzacija(ID_KONVERZACIJE)).thenReturn(List.of());

        List<KonverzacijaDto> lista = porukaService.konverzacije("iva");

        Assertions.assertEquals(1, lista.size());
        Mockito.verify(konverzacijaRepository, Mockito.never()).findByKorisnik(Mockito.anyLong());
    }

    @Test
    public void brojNeprocitanihZaKorisnikaTest() {
        Mockito.when(porukaRepository.brojNeprocitanihZaKorisnika(korisnik.getIdKorisnika()))
                .thenReturn(3L);

        Assertions.assertEquals(3L, porukaService.brojNeprocitanih("ana"));
        Mockito.verify(porukaRepository, Mockito.never())
                .brojNeprocitanihZaAdmina(Mockito.anyLong());
    }

    @Test
    public void brojNeprocitanihZaAdminaTest() {
        Mockito.when(porukaRepository.brojNeprocitanihZaAdmina(admin.getIdKorisnika()))
                .thenReturn(7L);

        Assertions.assertEquals(7L, porukaService.brojNeprocitanih("iva"));
        Mockito.verify(porukaRepository, Mockito.never())
                .brojNeprocitanihZaKorisnika(Mockito.anyLong());
    }
}
