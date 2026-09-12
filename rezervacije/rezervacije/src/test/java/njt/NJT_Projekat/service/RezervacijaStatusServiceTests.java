package njt.NJT_Projekat.service;

import java.time.LocalDateTime;
import java.util.List;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.entity.enums.StatusRezervacije;
import njt.NJT_Projekat.repository.impl.RezervacijaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class RezervacijaStatusServiceTests {

    @Autowired
    private RezervacijaStatusService rezervacijaStatusService;

    @MockitoBean
    private RezervacijaRepository rezervacijaRepository;

    private Rezervacija rezervacija(LocalDateTime pocetak, LocalDateTime kraj, StatusRezervacije status) {
        Rezervacija r = new Rezervacija();
        r.setIdRezervacije(1L);
        r.setPocetak(pocetak);
        r.setKraj(kraj);
        r.setStatus(status);
        return r;
    }

    @Test
    public void potvrdjenaKojaTrajePostajeUTokuTest() {
        //Construct
        Rezervacija r = rezervacija(
                LocalDateTime.now().minusMinutes(30),
                LocalDateTime.now().plusMinutes(30),
                StatusRezervacije.POTVRDJENA);
        Mockito.when(rezervacijaRepository.findAll()).thenReturn(List.of(r));
        //Act
        rezervacijaStatusService.azurirajStatuseRezervacija();
        //Assert
        Assertions.assertEquals(StatusRezervacije.U_TOKU, r.getStatus());
        Mockito.verify(rezervacijaRepository, Mockito.times(1)).save(r);
    }

    @Test
    public void prosaoTerminPostajeZavrsenaTest() {
        Rezervacija r = rezervacija(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().minusHours(1),
                StatusRezervacije.POTVRDJENA);
        Mockito.when(rezervacijaRepository.findAll()).thenReturn(List.of(r));

        rezervacijaStatusService.azurirajStatuseRezervacija();

        Assertions.assertEquals(StatusRezervacije.ZAVRSENA, r.getStatus());
        Mockito.verify(rezervacijaRepository, Mockito.times(1)).save(r);
    }

    @Test
    public void uTokuPoIstekuPostajeZavrsenaTest() {
        Rezervacija r = rezervacija(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().minusHours(1),
                StatusRezervacije.U_TOKU);
        Mockito.when(rezervacijaRepository.findAll()).thenReturn(List.of(r));

        rezervacijaStatusService.azurirajStatuseRezervacija();

        Assertions.assertEquals(StatusRezervacije.ZAVRSENA, r.getStatus());
    }

    @Test
    public void buducaRezervacijaOstajeNepromenjenaTest() {
        Rezervacija r = rezervacija(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                StatusRezervacije.POTVRDJENA);
        Mockito.when(rezervacijaRepository.findAll()).thenReturn(List.of(r));

        rezervacijaStatusService.azurirajStatuseRezervacija();

        Assertions.assertEquals(StatusRezervacije.POTVRDJENA, r.getStatus());
        Mockito.verify(rezervacijaRepository, Mockito.never()).save(Mockito.any(Rezervacija.class));
    }

    @Test
    public void otkazanaOstajeOtkazanaTest() {
        // otkazanu rezervaciju posao preskace, iako joj je termin odavno prosao
        Rezervacija r = rezervacija(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().minusHours(1),
                StatusRezervacije.OTKAZANA);
        Mockito.when(rezervacijaRepository.findAll()).thenReturn(List.of(r));

        rezervacijaStatusService.azurirajStatuseRezervacija();

        Assertions.assertEquals(StatusRezervacije.OTKAZANA, r.getStatus());
        Mockito.verify(rezervacijaRepository, Mockito.never()).save(Mockito.any(Rezervacija.class));
    }

    @Test
    public void zavrsenaSeNeDiraTest() {
        Rezervacija r = rezervacija(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().minusHours(1),
                StatusRezervacije.ZAVRSENA);
        Mockito.when(rezervacijaRepository.findAll()).thenReturn(List.of(r));

        rezervacijaStatusService.azurirajStatuseRezervacija();

        Mockito.verify(rezervacijaRepository, Mockito.never()).save(Mockito.any(Rezervacija.class));
    }

    @Test
    public void naCekanjuNePrelaziUTokuTest() {
        // u u_toku prelazi samo potvrdjena; na_cekanju ostaje kako jeste
        Rezervacija r = rezervacija(
                LocalDateTime.now().minusMinutes(30),
                LocalDateTime.now().plusMinutes(30),
                StatusRezervacije.NA_CEKANJU);
        Mockito.when(rezervacijaRepository.findAll()).thenReturn(List.of(r));

        rezervacijaStatusService.azurirajStatuseRezervacija();

        Assertions.assertEquals(StatusRezervacije.NA_CEKANJU, r.getStatus());
        Mockito.verify(rezervacijaRepository, Mockito.never()).save(Mockito.any(Rezervacija.class));
    }

    @Test
    public void viseRezervacijaOdjednomTest() {
        Rezervacija utoku = rezervacija(
                LocalDateTime.now().minusMinutes(30),
                LocalDateTime.now().plusMinutes(30),
                StatusRezervacije.POTVRDJENA);
        Rezervacija zavrsena = rezervacija(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().minusHours(1),
                StatusRezervacije.POTVRDJENA);
        Rezervacija buduca = rezervacija(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                StatusRezervacije.POTVRDJENA);
        Mockito.when(rezervacijaRepository.findAll())
                .thenReturn(List.of(utoku, zavrsena, buduca));

        rezervacijaStatusService.azurirajStatuseRezervacija();

        Assertions.assertEquals(StatusRezervacije.U_TOKU, utoku.getStatus());
        Assertions.assertEquals(StatusRezervacije.ZAVRSENA, zavrsena.getStatus());
        Assertions.assertEquals(StatusRezervacije.POTVRDJENA, buduca.getStatus());
        Mockito.verify(rezervacijaRepository, Mockito.times(2)).save(Mockito.any(Rezervacija.class));
    }
}
