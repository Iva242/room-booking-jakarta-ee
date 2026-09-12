package njt.NJT_Projekat.service;

import java.util.List;
import java.util.Optional;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.entity.auth.KorisnikVerificationToken;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.repository.auth.KorisnikVerificationTokenRepository;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class KorisnikServiceTests {

    @Autowired
    private KorisnikService korisnikService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private KorisnikRepository korisnikRepository;

    @MockitoBean
    private KorisnikVerificationTokenRepository tokenRepository;

    // mejl se ne salje zaista; bez ovoga bi servis pokusao da se poveze na SMTP
    @MockitoBean
    private JavaMailSender mailSender;

    private static final String LOZINKA = "tajnaLozinka123";

    private Korisnik noviKorisnik;

    @BeforeEach
    public void pripremiPodatke() {
        noviKorisnik = new Korisnik();
        noviKorisnik.setIme("Ana");
        noviKorisnik.setPrezime("Peric");
        noviKorisnik.setKorisnickoIme("ana1234");
        noviKorisnik.setLozinka(LOZINKA);
        noviKorisnik.setEmail("ana@primer.rs");
    }

    private Korisnik postojeciKorisnik(boolean aktivan) {
        Korisnik korisnik = new Korisnik();
        korisnik.setIdKorisnika(1L);
        korisnik.setIme("Ana");
        korisnik.setPrezime("Peric");
        korisnik.setKorisnickoIme("ana1234");
        korisnik.setLozinka(passwordEncoder.encode(LOZINKA));
        korisnik.setEmail("ana@primer.rs");
        korisnik.setUloga("ROLE_KORISNIK");
        korisnik.setAktivan(aktivan);
        return korisnik;
    }

    @Test
    public void registrujKorisnikaTest() {
        //Construct
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234")).thenReturn(Optional.empty());
        //Act
        KorisnikDto sacuvan = korisnikService.registrujKorisnika(noviKorisnik);
        //Assert
        assertNotNull(sacuvan);
        Assertions.assertEquals("ana1234", sacuvan.getKorisnickoIme());
        Assertions.assertEquals("ROLE_KORISNIK", sacuvan.getUloga());
    }

    @Test
    public void registrujKorisnikaLozinkaSeHesiraTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234")).thenReturn(Optional.empty());

        korisnikService.registrujKorisnika(noviKorisnik);

        ArgumentCaptor<Korisnik> uhvacen = ArgumentCaptor.forClass(Korisnik.class);
        Mockito.verify(korisnikRepository).save(uhvacen.capture());
        Korisnik upisan = uhvacen.getValue();

        Assertions.assertNotEquals(LOZINKA, upisan.getLozinka());
        Assertions.assertTrue(passwordEncoder.matches(LOZINKA, upisan.getLozinka()));
    }

    @Test
    public void registrujKorisnikaNalogNijeAktivanTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234")).thenReturn(Optional.empty());

        korisnikService.registrujKorisnika(noviKorisnik);

        ArgumentCaptor<Korisnik> uhvacen = ArgumentCaptor.forClass(Korisnik.class);
        Mockito.verify(korisnikRepository).save(uhvacen.capture());
        // nalog ostaje neaktivan dok se ne klikne na link iz mejla
        Assertions.assertFalse(uhvacen.getValue().isAktivan());
    }

    @Test
    public void registrujKorisnikaSaljeVerifikacioniMejlTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234")).thenReturn(Optional.empty());

        korisnikService.registrujKorisnika(noviKorisnik);

        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));
        Mockito.verify(tokenRepository, Mockito.times(1))
                .save(Mockito.any(KorisnikVerificationToken.class));
    }

    @Test
    public void registrujKorisnikaZauzetoImeFailureTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234"))
                .thenReturn(Optional.of(postojeciKorisnik(true)));

        RuntimeException greska = Assertions.assertThrows(RuntimeException.class, () -> {
            korisnikService.registrujKorisnika(noviKorisnik);
        });
        Assertions.assertEquals("Korisničko ime je zauzeto!", greska.getMessage());
        Mockito.verify(korisnikRepository, Mockito.never()).save(Mockito.any(Korisnik.class));
    }

    @Test
    public void loginTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234"))
                .thenReturn(Optional.of(postojeciKorisnik(true)));

        KorisnikDto prijavljen = korisnikService.login("ana1234", LOZINKA);

        assertNotNull(prijavljen);
        Assertions.assertEquals("ana1234", prijavljen.getKorisnickoIme());
    }

    @Test
    public void loginNeverifikovanNalogFailureTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234"))
                .thenReturn(Optional.of(postojeciKorisnik(false)));

        RuntimeException greska = Assertions.assertThrows(RuntimeException.class, () -> {
            korisnikService.login("ana1234", LOZINKA);
        });
        Assertions.assertTrue(greska.getMessage().contains("nije verifikovan"));
    }

    @Test
    public void loginPogresnaLozinkaFailureTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("ana1234"))
                .thenReturn(Optional.of(postojeciKorisnik(true)));

        RuntimeException greska = Assertions.assertThrows(RuntimeException.class, () -> {
            korisnikService.login("ana1234", "pogresna");
        });
        Assertions.assertEquals("Pogrešna lozinka!", greska.getMessage());
    }

    @Test
    public void loginNepostojeciKorisnikFailureTest() {
        Mockito.when(korisnikRepository.findByKorisnickoIme("nepostoji")).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            korisnikService.login("nepostoji", LOZINKA);
        });
    }

    @Test
    public void verifikujKorisnikaTest() {
        Korisnik korisnik = postojeciKorisnik(false);
        KorisnikVerificationToken token = KorisnikVerificationToken.of(korisnik, 3600);
        Mockito.when(tokenRepository.findByToken(token.getToken())).thenReturn(token);

        korisnikService.verifikujKorisnika(token.getToken());

        Assertions.assertTrue(korisnik.isAktivan());
        Mockito.verify(korisnikRepository, Mockito.times(1)).save(korisnik);
        Mockito.verify(tokenRepository, Mockito.times(1)).delete(token);
    }

    @Test
    public void verifikujKorisnikaIstekaoTokenFailureTest() {
        Korisnik korisnik = postojeciKorisnik(false);
        // negativno trajanje pravi token kome je rok istekao
        KorisnikVerificationToken token = KorisnikVerificationToken.of(korisnik, -3600);
        Mockito.when(tokenRepository.findByToken(token.getToken())).thenReturn(token);

        RuntimeException greska = Assertions.assertThrows(RuntimeException.class, () -> {
            korisnikService.verifikujKorisnika(token.getToken());
        });
        Assertions.assertTrue(greska.getMessage().contains("istekao"));
        Assertions.assertFalse(korisnik.isAktivan());
    }

    @Test
    public void verifikujKorisnikaNevalidanTokenFailureTest() {
        Mockito.when(tokenRepository.findByToken("nepostojeci")).thenReturn(null);

        Assertions.assertThrows(RuntimeException.class, () -> {
            korisnikService.verifikujKorisnika("nepostojeci");
        });
    }

    @Test
    public void korisniciVracaSamoObicneKorisnikeTest() {
        Mockito.when(korisnikRepository.findByUloga("ROLE_KORISNIK"))
                .thenReturn(List.of(postojeciKorisnik(true)));

        List<KorisnikDto> korisnici = korisnikService.korisnici();

        Assertions.assertEquals(1, korisnici.size());
        Assertions.assertEquals("ROLE_KORISNIK", korisnici.get(0).getUloga());
    }
}
