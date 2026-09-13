package njt.NJT_Projekat.controller;

import java.time.LocalDateTime;
import java.util.List;
import njt.NJT_Projekat.config.SecurityConfig;
import njt.NJT_Projekat.dto.impl.KonverzacijaDto;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.dto.impl.NovaPorukaDto;
import njt.NJT_Projekat.dto.impl.PorukaDto;
import njt.NJT_Projekat.exception.GlobalniObradjivacGresaka;
import njt.NJT_Projekat.exception.NemaDozvoluException;
import njt.NJT_Projekat.security.AppUserDetailsService;
import njt.NJT_Projekat.security.JwtService;
import njt.NJT_Projekat.service.PorukaService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PorukaController.class)
@Import({SecurityConfig.class, GlobalniObradjivacGresaka.class})
public class PorukaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PorukaService porukaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    private RequestPostProcessor korisnik() {
        return SecurityMockMvcRequestPostProcessors.user("ana").roles("KORISNIK");
    }

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("iva").roles("ADMIN");
    }

    private KonverzacijaDto konverzacijaDto() {
        KorisnikDto k = new KorisnikDto();
        k.setId(1L);
        k.setKorisnickoIme("ana");

        KonverzacijaDto d = new KonverzacijaDto();
        d.setIdKonverzacije(5L);
        d.setKorisnik(k);
        d.setIdSale(2L);
        d.setNazivSale("Plavi salon");
        d.setPoslednjaAktivnost(LocalDateTime.of(2026, 9, 13, 14, 0));
        d.setPregledPoslednjePoruke("Koja je cena?");
        d.setBrojNeprocitanih(1);
        return d;
    }

    private PorukaDto porukaDto() {
        KorisnikDto k = new KorisnikDto();
        k.setId(1L);
        k.setKorisnickoIme("ana");

        PorukaDto d = new PorukaDto();
        d.setIdPoruke(9L);
        d.setIdKonverzacije(5L);
        d.setTekst("Koja je cena?");
        d.setVreme(LocalDateTime.of(2026, 9, 13, 14, 0));
        d.setAutor(k);
        return d;
    }

    private String telo(Long idSale, String tekst) {
        NovaPorukaDto z = new NovaPorukaDto();
        z.setIdSale(idSale);
        z.setTekst(tekst);
        return objectMapper.writeValueAsString(z);
    }

    @Test
    public void konverzacijeTest() throws Exception {
        Mockito.when(porukaService.konverzacije("ana")).thenReturn(List.of(konverzacijaDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/poruke").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nazivSale",
                        CoreMatchers.equalTo("Plavi salon")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].brojNeprocitanih",
                        CoreMatchers.equalTo(1)));
    }

    @Test
    public void konverzacijeBezPrijaveFailureTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/poruke"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(porukaService, Mockito.never()).konverzacije(Mockito.anyString());
    }

    @Test
    public void zapocniTest() throws Exception {
        Mockito.when(porukaService.zapocniKonverzaciju(Mockito.any(NovaPorukaDto.class),
                Mockito.eq("ana"))).thenReturn(konverzacijaDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/poruke")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(telo(2L, "Koja je cena?"))
                .with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.idKonverzacije",
                        CoreMatchers.equalTo(5)));
    }

    @Test
    public void zapocniPraznuPorukuFailureTest() throws Exception {
        // presrece @Valid, pre nego sto se servis uopste pozove
        mockMvc.perform(MockMvcRequestBuilders.post("/api/poruke")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(telo(2L, "   "))
                .with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(porukaService, Mockito.never())
                .zapocniKonverzaciju(Mockito.any(NovaPorukaDto.class), Mockito.anyString());
    }

    @Test
    public void zapocniKaoAdminFailureTest() throws Exception {
        Mockito.when(porukaService.zapocniKonverzaciju(Mockito.any(NovaPorukaDto.class),
                Mockito.eq("iva")))
                .thenThrow(new NemaDozvoluException("Razgovor može započeti samo korisnik."));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/poruke")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(telo(2L, "zdravo"))
                .with(admin()))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.poruka",
                        CoreMatchers.equalTo("Razgovor može započeti samo korisnik.")));
    }

    @Test
    public void porukeTest() throws Exception {
        Mockito.when(porukaService.poruke(5L, "ana")).thenReturn(List.of(porukaDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/poruke/5").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tekst",
                        CoreMatchers.equalTo("Koja je cena?")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].autor.korisnickoIme",
                        CoreMatchers.equalTo("ana")));
    }

    @Test
    public void porukeTudjegRazgovoraFailureTest() throws Exception {
        Mockito.when(porukaService.poruke(5L, "ana"))
                .thenThrow(new NemaDozvoluException("Nemate pristup ovom razgovoru."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/poruke/5").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.equalTo(403)));
    }

    @Test
    public void odgovoriTest() throws Exception {
        Mockito.when(porukaService.posaljiPoruku(Mockito.eq(5L),
                Mockito.any(NovaPorukaDto.class), Mockito.eq("iva"))).thenReturn(porukaDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/poruke/5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(telo(null, "1400 dinara po osobi"))
                .with(admin()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void brojNeprocitanihTest() throws Exception {
        Mockito.when(porukaService.brojNeprocitanih("ana")).thenReturn(3L);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/poruke/neprocitane").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.broj", CoreMatchers.equalTo(3)));
    }
}
