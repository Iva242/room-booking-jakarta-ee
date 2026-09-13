package njt.NJT_Projekat.service;

import jakarta.transaction.Transactional;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.exception.NemaDozvoluException;
import njt.NJT_Projekat.exception.NevalidanZahtevException;
import njt.NJT_Projekat.exception.TerminZauzetException;
import njt.NJT_Projekat.entity.auth.KorisnikVerificationToken;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import njt.NJT_Projekat.repository.auth.KorisnikVerificationTokenRepository;
import njt.NJT_Projekat.mapper.impl.KorisnikMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage; // Dodato
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KorisnikService {

    private static final String ULOGA_KORISNIK = "ROLE_KORISNIK";

    private final KorisnikRepository korisnikRepository;
    private final KorisnikMapper korisnikMapper;
    private final KorisnikVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; 

    @Autowired
    public KorisnikService(KorisnikRepository korisnikRepository, KorisnikMapper korisnikMapper,
            KorisnikVerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder, JavaMailSender mailSender) { 
        this.korisnikRepository = korisnikRepository;
        this.korisnikMapper = korisnikMapper;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender; 
    }

    @Transactional
    public KorisnikDto registrujKorisnika(Korisnik korisnik) {
        Optional<Korisnik> postojeci = korisnikRepository.findByKorisnickoIme(korisnik.getKorisnickoIme());
        if (postojeci.isPresent()) {
            throw new TerminZauzetException("Korisničko ime je zauzeto!");
        }

        if (korisnik.getUloga() == null || korisnik.getUloga().isEmpty()) {
            korisnik.setUloga(ULOGA_KORISNIK);
        }

        korisnik.setLozinka(passwordEncoder.encode(korisnik.getLozinka()));
        korisnik.setAktivan(false);

        korisnikRepository.save(korisnik);

        // generisemo token
        KorisnikVerificationToken token = KorisnikVerificationToken.of(korisnik, 86400);
        tokenRepository.save(token);

        
        posaljiVerifikacioniMejl(korisnik.getEmail(), token.getToken());

        return korisnikMapper.toDto(korisnik);
    }

   
    private void posaljiVerifikacioniMejl(String primalac, String tokenValue) {
        String link = "http://localhost:8080/api/auth/verify?token=" + tokenValue;

        SimpleMailMessage poruka = new SimpleMailMessage();
        poruka.setTo(primalac);
        poruka.setSubject("Verifikacija naloga - Rezervacija Sala");
        poruka.setText("Poštovani,\n\nMolimo vas da verifikujete vaš nalog klikom na sledeći link:\n" + link + "\n\nPozdrav,\nTim za rezervaciju sala");

        mailSender.send(poruka);
    }

    @Transactional
    public void verifikujKorisnika(String tokenValue) {
        KorisnikVerificationToken token = tokenRepository.findByToken(tokenValue);
        if (token == null) {
            throw new NevalidanZahtevException("Nevalidan verifikacioni token!");
        }
        if (token.isExpired()) {
            tokenRepository.delete(token);
            throw new NevalidanZahtevException("Verifikacioni token je istekao!");
        }

        Korisnik korisnik = token.getKorisnik();
        korisnik.setAktivan(true);
        korisnikRepository.save(korisnik);

        tokenRepository.delete(token);
    }

    public KorisnikDto login(String korisnickoIme, String lozinka) {
        Korisnik korisnik = korisnikRepository.findByKorisnickoIme(korisnickoIme)
                .orElseThrow(() -> new EntitetNijePronadjenException("Korisnik sa tim korisničkim imenom ne postoji!"));

        if (!korisnik.isAktivan()) {
            throw new NemaDozvoluException("Nalog nije verifikovan! Molimo potvrdite vašu registraciju klikom na link.");
        }

        if (!passwordEncoder.matches(lozinka, korisnik.getLozinka())) { 
            throw new NevalidanZahtevException("Pogrešna lozinka!");
        }

        return korisnikMapper.toDto(korisnik);
    }

    // prazna pretraga vraca istu listu koju daje i korisnici(), pa se prikaz
    // ponasa isto kao pre unosa teksta
    public List<KorisnikDto> pretraziKlijente(String tekst) {
        if (tekst == null || tekst.isBlank()) {
            return korisnici();
        }
        return korisnikRepository.pretraziPoUlozi(tekst.trim(), ULOGA_KORISNIK).stream()
                .map(korisnikMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<KorisnikDto> korisnici() {
        return korisnikRepository.findByUloga(ULOGA_KORISNIK).stream()
                .map(korisnikMapper::toDto)
                .collect(Collectors.toList());
    }
}
