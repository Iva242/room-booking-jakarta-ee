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
        System.out.println("--- PRIMLJEN ZAHTEV ZA VRATI SVE ---");
        return ResponseEntity.ok(salaService.vratiSve());
    }

    // Pregled jedne sale: GET http://localhost:8080/api/sale/1
    @GetMapping("/{id}")
    public ResponseEntity<?> vratiPoId(@PathVariable Long id) {
        try {
            SalaDto sala = salaService.vratiPoId(id);
            return ResponseEntity.ok(sala);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ruta za pretragu sala: GET http://localhost:8080/api/sale/pretraga?term=Beograd
    @GetMapping("/pretraga")
    public ResponseEntity<List<SalaDto>> pretrazi(@RequestParam String term) {
        return ResponseEntity.ok(salaService.pretraziSale(term));
    }

    // ruta za dodavanje nove sale: POST http://localhost:8080/api/sale
    @PostMapping
    public ResponseEntity<?> kreiraj(@RequestBody SalaDto salaDto) {
        try {
            SalaDto nova = salaService.kreiraj(salaDto);
            return ResponseEntity.ok(nova);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ruta za izmenu sale: PUT http://localhost:8080/api/sale/1
    @PutMapping("/{id}")
    public ResponseEntity<?> izmeni(@PathVariable Long id, @RequestBody SalaDto salaDto) {
        try {
            SalaDto izmenjena = salaService.izmeni(id, salaDto);
            return ResponseEntity.ok(izmenjena);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ruta za brisanje sale: DELETE http://localhost:8080/api/sale/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> obrisi(@PathVariable Long id) {
        try {
            salaService.obrisi(id);
            return ResponseEntity.ok("Sala je uspešno obrisana.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}