package njt.NJT_Projekat.controller;

import njt.NJT_Projekat.config.SecurityConfig;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.exception.GlobalniObradjivacGresaka;
import njt.NJT_Projekat.exception.TerminZauzetException;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import njt.NJT_Projekat.security.AppUserDetailsService;
import njt.NJT_Projekat.security.JwtService;
import njt.NJT_Projekat.service.KorisnikService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalniObradjivacGresaka.class})
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private KorisnikService korisnikService;

    @MockitoBean
    private KorisnikRepository korisnikRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    private static final String PRIJAVA =
            "{\"korisnickoIme\":\"ana1234\",\"lozinka\":\"tajna123\"}";

    @Test
    public void loginPogresniPodaciFailureTest() throws Exception {
        Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(PRIJAVA))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.equalTo(401)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.poruka",
                        CoreMatchers.equalTo("Pogrešno korisničko ime ili lozinka.")));
    }

    @Test
    public void loginBezLozinkeFailureTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"korisnickoIme\":\"ana1234\",\"lozinka\":\"\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(authenticationManager, Mockito.never())
                .authenticate(Mockito.any(Authentication.class));
    }

    @Test
    public void registerTest() throws Exception {
        KorisnikDto kreiran = new KorisnikDto();
        kreiran.setId(1L);
        kreiran.setKorisnickoIme("ana1234");
        kreiran.setUloga("ROLE_KORISNIK");
        Mockito.when(korisnikService.registrujKorisnika(Mockito.any())).thenReturn(kreiran);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"korisnickoIme\":\"ana1234\",\"email\":\"ana@primer.rs\","
                        + "\"lozinka\":\"tajna123\",\"ime\":\"Ana\",\"prezime\":\"Perić\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.korisnickoIme",
                        CoreMatchers.equalTo("ana1234")));
    }

    @Test
    public void registerZauzetoImeFailureTest() throws Exception {
        Mockito.when(korisnikService.registrujKorisnika(Mockito.any()))
                .thenThrow(new TerminZauzetException("Korisničko ime je zauzeto!"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"korisnickoIme\":\"ana1234\",\"email\":\"ana@primer.rs\","
                        + "\"lozinka\":\"tajna123\",\"ime\":\"Ana\",\"prezime\":\"Perić\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.poruka",
                        CoreMatchers.equalTo("Korisničko ime je zauzeto!")));
    }

    @Test
    public void registerNeispravanMejlFailureTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"korisnickoIme\":\"ana1234\",\"email\":\"nijemejl\","
                        + "\"lozinka\":\"tajna123\",\"ime\":\"Ana\",\"prezime\":\"Perić\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(korisnikService, Mockito.never()).registrujKorisnika(Mockito.any());
    }
}
