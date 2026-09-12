package njt.NJT_Projekat.controller;

import njt.NJT_Projekat.dto.impl.KarakteristikaDto;
import njt.NJT_Projekat.service.KarakteristikaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/karakteristike")
@CrossOrigin(origins = "http://localhost:3000")
public class KarakteristikaController {

    private final KarakteristikaService karakteristikaService;

    @Autowired
    public KarakteristikaController(KarakteristikaService karakteristikaService) {
        this.karakteristikaService = karakteristikaService;
    }

    // GET http://localhost:8080/api/karakteristike
    @GetMapping
    public ResponseEntity<List<KarakteristikaDto>> vratiSve() {
        return ResponseEntity.ok(karakteristikaService.vratiSve());
    }

    // POST http://localhost:8080/api/karakteristike
    @PostMapping
    public ResponseEntity<?> kreiraj(@RequestBody KarakteristikaDto karakteristikaDto) {
        try {
            
            KarakteristikaDto nova = karakteristikaService.kreirajKarakteristiku(karakteristikaDto);
            return ResponseEntity.ok(nova);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
