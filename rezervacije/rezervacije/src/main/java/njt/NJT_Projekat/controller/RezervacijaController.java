package njt.NJT_Projekat.controller;

import java.security.Principal;
import njt.NJT_Projekat.dto.impl.RezervacijaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import njt.NJT_Projekat.service.RezervacijaService;

@RestController
@RequestMapping("/api/rezervacije") // ovako nam pocinju sve rute za rezervacije
@CrossOrigin(origins = "http://localhost:3000")
public class RezervacijaController {

    private final RezervacijaService rezervacijaService;

    @Autowired
    public RezervacijaController(RezervacijaService rezervacijaService) {
        this.rezervacijaService = rezervacijaService;
    }

    // kreiranje rezervacije: POST http://localhost:8080/api/rezervacije
    @PostMapping
    public ResponseEntity<?> kreiraj(@RequestBody RezervacijaDto rezervacijaDto) {
        
        System.out.println("Primljen DTO: " + rezervacijaDto);
        if (rezervacijaDto.getIdSale()== null) {
            System.out.println("UPOZORENJE: Sala je NULL!");
        }
        try {
            RezervacijaDto nova = rezervacijaService.kreirajRezervaciju(rezervacijaDto);
            return ResponseEntity.ok(nova);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // pregled svih rezervacija: GET http://localhost:8080/api/rezervacije
    @GetMapping
    public ResponseEntity<List<RezervacijaDto>> vratiSve() {
        return ResponseEntity.ok(rezervacijaService.rezervacije());
    }

    // pregled sopstvenih rezervacija: GET http://localhost:8080/api/rezervacije/moje
    @GetMapping("/moje")
    public ResponseEntity<List<RezervacijaDto>> vratiMojeRezervacije(Principal principal) {
        // Principal.getName() ti vraća username (ili email) ulogovanog korisnika
        return ResponseEntity.ok(rezervacijaService.vratiRezervacijeKorisnika(principal.getName()));
    }

    // otkazivanje rezervacije: DELETE http://localhost:8080/api/rezervacije/{id}
    // (Korisnik otkazuje svoju, Admin može otkazati bilo koju)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> otkazi(@PathVariable Long id, Principal principal) {
        try {
            // Prosleđujemo i ID rezervacije i ko je trenutno ulogovan
            rezervacijaService.otkaziRezervaciju(id, principal.getName());
            return ResponseEntity.ok("Rezervacija je uspešno otkazana.");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
        }
    }

    // potvrđivanje rezervacije: PUT http://localhost:8080/api/rezervacije/potvrdi/5
    @PutMapping("/potvrdi/{id}")
    public ResponseEntity<?> potvrdi(@PathVariable Long id) {
        try {
            rezervacijaService.potvrdiRezervaciju(id);
            return ResponseEntity.ok("Rezervacija je uspešno potvrđena.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
