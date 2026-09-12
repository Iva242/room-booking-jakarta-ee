package njt.NJT_Projekat.controller;

import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import njt.NJT_Projekat.config.SecurityConfig;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.dto.impl.RezervacijaDto;
import njt.NJT_Projekat.dto.impl.TipDogadjajaDto;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.exception.GlobalniObradjivacGresaka;
import njt.NJT_Projekat.exception.NemaDozvoluException;
import njt.NJT_Projekat.exception.TerminZauzetException;
import njt.NJT_Projekat.entity.enums.StatusRezervacije;
import njt.NJT_Projekat.security.AppUserDetailsService;
import njt.NJT_Projekat.security.JwtService;
import njt.NJT_Projekat.service.RezervacijaService;
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

@WebMvcTest(RezervacijaController.class)
@Import({SecurityConfig.class, GlobalniObradjivacGresaka.class})
public class RezervacijaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RezervacijaService rezervacijaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    private static final LocalDateTime POCETAK = LocalDateTime.of(2030, 5, 10, 10, 0);
    private static final LocalDateTime KRAJ = LocalDateTime.of(2030, 5, 10, 12, 0);

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("iva").roles("ADMIN");
    }

    private RequestPostProcessor korisnik() {
        return SecurityMockMvcRequestPostProcessors.user("ana").roles("KORISNIK");
    }

    private RezervacijaDto rezervacijaDto() {
        KorisnikDto korisnikDto = new KorisnikDto();
        korisnikDto.setId(7L);
        korisnikDto.setKorisnickoIme("ana");

        TipDogadjajaDto tipDto = new TipDogadjajaDto();
        tipDto.setIdTipDogadjaja(3L);

        RezervacijaDto dto = new RezervacijaDto();
        dto.setIdRezervacije(10L);
        dto.setIdSale(1L);
        dto.setKorisnik(korisnikDto);
        dto.setTipDogadjaja(tipDto);
        dto.setPocetak(POCETAK);
        dto.setKraj(KRAJ);
        dto.setStatus(StatusRezervacije.NA_CEKANJU);
        return dto;
    }

    @Test
    public void kreirajTest() throws Exception {
        RezervacijaDto dto = rezervacijaDto();
        Mockito.when(rezervacijaService.kreirajRezervaciju(Mockito.any(RezervacijaDto.class)))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/rezervacije")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto))
                .with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.idRezervacije",
                        CoreMatchers.equalTo(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status",
                        CoreMatchers.equalTo("na_cekanju")));
    }

    @Test
    public void kreirajZauzetTerminFailureTest() throws Exception {
        RezervacijaDto dto = rezervacijaDto();
        Mockito.when(rezervacijaService.kreirajRezervaciju(Mockito.any(RezervacijaDto.class)))
                .thenThrow(new TerminZauzetException("Sala je zauzeta u tom terminu."));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/rezervacije")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto))
                .with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.poruka",
                        CoreMatchers.equalTo("Sala je zauzeta u tom terminu.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.equalTo(409)));
    }

    @Test
    public void kreirajBezPrijaveFailureTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/rezervacije")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(rezervacijaDto())))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(rezervacijaService, Mockito.never())
                .kreirajRezervaciju(Mockito.any(RezervacijaDto.class));
    }

    @Test
    public void kreirajZaNepostojecuSaluFailureTest() throws Exception {
        Mockito.when(rezervacijaService.kreirajRezervaciju(Mockito.any(RezervacijaDto.class)))
                .thenThrow(new EntitetNijePronadjenException("Sala nije pronađena."));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/rezervacije")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(rezervacijaDto()))
                .with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.equalTo(404)));
    }

    @Test
    public void vratiSveTest() throws Exception {
        Mockito.when(rezervacijaService.rezervacije()).thenReturn(List.of(rezervacijaDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/rezervacije").with(admin()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idRezervacije",
                        CoreMatchers.equalTo(10)));
    }

    @Test
    public void vratiSveKaoObicanKorisnikFailureTest() throws Exception {
        // pregled svih rezervacija je samo za administratora
        mockMvc.perform(MockMvcRequestBuilders.get("/api/rezervacije").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(rezervacijaService, Mockito.never()).rezervacije();
    }

    @Test
    public void vratiMojeRezervacijeTest() throws Exception {
        Mockito.when(rezervacijaService.vratiRezervacijeKorisnika("ana"))
                .thenReturn(List.of(rezervacijaDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/rezervacije/moje").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idRezervacije",
                        CoreMatchers.equalTo(10)));
    }

    @Test
    public void potvrdiTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/rezervacije/potvrdi/10").with(admin()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(rezervacijaService, Mockito.times(1)).potvrdiRezervaciju(10L);
    }

    @Test
    public void potvrdiKaoObicanKorisnikFailureTest() throws Exception {
        // potvrdjivanje tudje rezervacije nije posao obicnog korisnika
        mockMvc.perform(MockMvcRequestBuilders.put("/api/rezervacije/potvrdi/10").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(rezervacijaService, Mockito.never()).potvrdiRezervaciju(Mockito.anyLong());
    }

    @Test
    public void otkaziTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/rezervacije/10").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(rezervacijaService, Mockito.times(1)).otkaziRezervaciju(10L, "ana");
    }

    @Test
    public void otkaziTudjuRezervacijuFailureTest() throws Exception {
        Mockito.doThrow(new NemaDozvoluException("Nemate dozvolu da otkažete tuđu rezervaciju!"))
                .when(rezervacijaService).otkaziRezervaciju(10L, "ana");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/rezervacije/10").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.poruka",
                        CoreMatchers.equalTo("Nemate dozvolu da otkažete tuđu rezervaciju!")));
    }
}
