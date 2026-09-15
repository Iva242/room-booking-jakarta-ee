package njt.NJT_Projekat.controller;

import jakarta.validation.Valid;
import njt.NJT_Projekat.dto.auth.AuthRequest;
import njt.NJT_Projekat.dto.auth.AuthResponse;
import njt.NJT_Projekat.dto.auth.RegisterRequest;
import njt.NJT_Projekat.exception.NemaDozvoluException;
import njt.NJT_Projekat.security.JwtService;
import njt.NJT_Projekat.service.KorisnikService; // Uvezen servis
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final KorisnikService korisnikService; // Ubrizgavamo servis umesto repozitorijuma direktno

    @Autowired
    private KorisnikRepository korisnikRepository; // za potrebe login-a i izvlacenja ID-ja

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, KorisnikService korisnikService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.korisnikService = korisnikService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // Prvo pustimo Spring Security da odradi proveru 
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getKorisnickoIme(), request.getLozinka())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String uloga = userDetails.getAuthorities().iterator().next().getAuthority();

        Korisnik korisnik = korisnikRepository.findByKorisnickoIme(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen!"));

        // provera da li je aktivan
        if (!korisnik.isAktivan()) {
            throw new NemaDozvoluException(
                    "Nalog nije potvrđen. Proverite email za potvrdu naloga.");
        }

        Long praviId = korisnik.getIdKorisnika();
        String token = jwtService.generateTokenWithId(praviId, userDetails.getUsername(), uloga);

        KorisnikDto korisnikDto = new KorisnikDto();
        korisnikDto.setId(praviId);
        korisnikDto.setKorisnickoIme(userDetails.getUsername());
        korisnikDto.setUloga(uloga);
        korisnikDto.setIme(korisnik.getIme());
        korisnikDto.setPrezime(korisnik.getPrezime());

        return ResponseEntity.ok(new AuthResponse(token, korisnikDto));
    }

    @PostMapping("/register")
    public ResponseEntity<KorisnikDto> register(@Valid @RequestBody RegisterRequest request) {
        // pakujemo podatke iz RegisterRequest-a u entitet Korisnik
        Korisnik noviKorisnik = new Korisnik();
        noviKorisnik.setKorisnickoIme(request.getKorisnickoIme());
        noviKorisnik.setLozinka(request.getLozinka());
        noviKorisnik.setIme(request.getIme());
        noviKorisnik.setPrezime(request.getPrezime());
        noviKorisnik.setEmail(request.getEmail());

        // Korisnik servis nam obezbedjuje validaciju, enkripciju i generisanje tokena
        return ResponseEntity.ok(korisnikService.registrujKorisnika(noviKorisnik));
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        try {
            korisnikService.verifikujKorisnika(token);
            return ResponseEntity.ok("Uspešno verifikovan nalog! Možete se ulogovati.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
