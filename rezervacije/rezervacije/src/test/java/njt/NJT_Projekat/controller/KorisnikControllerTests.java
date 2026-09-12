package njt.NJT_Projekat.controller;

import java.util.List;
import njt.NJT_Projekat.config.SecurityConfig;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.security.AppUserDetailsService;
import njt.NJT_Projekat.security.JwtService;
import njt.NJT_Projekat.service.KorisnikService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(KorisnikController.class)
@Import(SecurityConfig.class)
public class KorisnikControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KorisnikService korisnikService;

    // JwtService i AppUserDetailsService su potrebni da bi se SecurityConfig podigao
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    // @WithMockUser ne radi uz SessionCreationPolicy.STATELESS jer lanac filtera
    // iznova ucitava kontekst i brise ono sto je test postavio; ovako se prijavljeni
    // korisnik upisuje u sam zahtev
    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("iva").roles("ADMIN");
    }

    private RequestPostProcessor korisnik() {
        return SecurityMockMvcRequestPostProcessors.user("ana").roles("KORISNIK");
    }

    private KorisnikDto korisnikDto() {
        KorisnikDto dto = new KorisnikDto();
        dto.setId(7L);
        dto.setIme("Ana");
        dto.setPrezime("Peric");
        dto.setKorisnickoIme("ana1234");
        dto.setUloga("ROLE_KORISNIK");
        return dto;
    }

    @Test
    public void vratiSveTest() throws Exception {
        Mockito.when(korisnikService.korisnici()).thenReturn(List.of(korisnikDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/korisnici").with(admin()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].korisnickoIme",
                        CoreMatchers.equalTo("ana1234")));
    }

    @Test
    public void vratiSveKaoObicanKorisnikFailureTest() throws Exception {
        // spisak svih naloga sme da vidi samo administrator
        mockMvc.perform(MockMvcRequestBuilders.get("/api/korisnici").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(korisnikService, Mockito.never()).korisnici();
    }

    @Test
    public void vratiSveBezPrijaveFailureTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/korisnici"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(korisnikService, Mockito.never()).korisnici();
    }

    @Test
    public void pretraziTest() throws Exception {
        Mockito.when(korisnikService.pretraziKlijente("ana")).thenReturn(List.of(korisnikDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/korisnici/pretraga")
                .param("term", "ana").with(admin()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ime", CoreMatchers.equalTo("Ana")));
    }

    @Test
    public void pretraziKaoObicanKorisnikFailureTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/korisnici/pretraga")
                .param("term", "ana").with(korisnik()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
