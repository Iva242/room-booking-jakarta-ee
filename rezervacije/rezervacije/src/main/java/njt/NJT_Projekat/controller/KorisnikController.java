package njt.NJT_Projekat.controller;

import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.service.KorisnikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/korisnici") // ovako nam pocinju sve rute u kontroleru
@CrossOrigin(origins = "http://localhost:3000")
public class KorisnikController {

    private final KorisnikService korisnikService;

    @Autowired
    public KorisnikController(KorisnikService korisnikService) {
        this.korisnikService = korisnikService;
    }

    // ruta za pregled svih korisnika: GET http://localhost:8080/api/korisnici
    @GetMapping
    public ResponseEntity<List<KorisnikDto>> vratiSve() {
        return ResponseEntity.ok(korisnikService.korisnici());
    }

    // ruta za pretragu klijenata: GET http://localhost:8080/api/korisnici/pretraga?term=Iva
    @GetMapping("/pretraga")
    public ResponseEntity<List<KorisnikDto>> pretrazi(@RequestParam String term) {
        List<KorisnikDto> rezultat = korisnikService.pretraziKlijente(term);
        return ResponseEntity.ok(rezultat);
    }
}
