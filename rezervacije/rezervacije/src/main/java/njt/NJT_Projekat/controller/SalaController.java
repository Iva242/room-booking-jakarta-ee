package njt.NJT_Projekat.controller;

import njt.NJT_Projekat.dto.impl.SalaDto;
import njt.NJT_Projekat.service.SalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
@CrossOrigin(origins = "http://localhost:3000")
public class SalaController {

    private final SalaService salaService;

    @Autowired
    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    // Pregled svih sala: GET http://localhost:8080/api/sale
    @GetMapping
    public ResponseEntity<List<SalaDto>> vratiSve() {
        return ResponseEntity.ok(salaService.vratiSve());
    }

    // Pregled jedne sale: GET http://localhost:8080/api/sale/1
    @GetMapping("/{id}")
    public ResponseEntity<SalaDto> vratiPoId(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.vratiPoId(id));
    }

    // ruta za pretragu sala: GET http://localhost:8080/api/sale/pretraga?term=Beograd
    @GetMapping("/pretraga")
    public ResponseEntity<List<SalaDto>> pretrazi(@RequestParam String term) {
        return ResponseEntity.ok(salaService.pretraziSale(term));
    }

    // ruta za dodavanje nove sale: POST http://localhost:8080/api/sale
    @PostMapping
    public ResponseEntity<SalaDto> kreiraj(@RequestBody SalaDto salaDto) {
        return ResponseEntity.ok(salaService.kreiraj(salaDto));
    }

    // ruta za izmenu sale: PUT http://localhost:8080/api/sale/1
    @PutMapping("/{id}")
    public ResponseEntity<SalaDto> izmeni(@PathVariable Long id, @RequestBody SalaDto salaDto) {
        return ResponseEntity.ok(salaService.izmeni(id, salaDto));
    }

    // ruta za brisanje sale: DELETE http://localhost:8080/api/sale/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> obrisi(@PathVariable Long id) {
        salaService.obrisi(id);
        return ResponseEntity.noContent().build();
    }
}