package njt.NJT_Projekat.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import njt.NJT_Projekat.dto.impl.KonverzacijaDto;
import njt.NJT_Projekat.dto.impl.NovaPorukaDto;
import njt.NJT_Projekat.dto.impl.PorukaDto;
import njt.NJT_Projekat.service.PorukaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poruke")
@CrossOrigin(origins = "http://localhost:3000")
public class PorukaController {

    private final PorukaService porukaService;

    @Autowired
    public PorukaController(PorukaService porukaService) {
        this.porukaService = porukaService;
    }

    // spisak razgovora: korisnik dobija svoje, administrator sve
    @GetMapping
    public ResponseEntity<List<KonverzacijaDto>> konverzacije(Principal principal) {
        return ResponseEntity.ok(porukaService.konverzacije(principal.getName()));
    }

    // novi razgovor, moze samo korisnik
    @PostMapping
    public ResponseEntity<KonverzacijaDto> zapocni(@Valid @RequestBody NovaPorukaDto zahtev,
            Principal principal) {
        return ResponseEntity.ok(porukaService.zapocniKonverzaciju(zahtev, principal.getName()));
    }

    // poruke jednog razgovora; otvaranjem se oznacavaju kao procitane
    @GetMapping("/{id}")
    public ResponseEntity<List<PorukaDto>> poruke(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(porukaService.poruke(id, principal.getName()));
    }

    // odgovor u postojecem razgovoru, i korisnik i administrator
    @PostMapping("/{id}")
    public ResponseEntity<PorukaDto> odgovori(@PathVariable Long id,
            @Valid @RequestBody NovaPorukaDto zahtev, Principal principal) {
        return ResponseEntity.ok(porukaService.posaljiPoruku(id, zahtev, principal.getName()));
    }

    // brojac za ikonicu u zaglavlju
    @GetMapping("/neprocitane")
    public ResponseEntity<Map<String, Long>> brojNeprocitanih(Principal principal) {
        return ResponseEntity.ok(Map.of("broj",
                porukaService.brojNeprocitanih(principal.getName())));
    }
}
