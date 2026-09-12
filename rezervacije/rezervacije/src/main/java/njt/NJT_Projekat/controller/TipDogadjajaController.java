package njt.NJT_Projekat.controller;

import njt.NJT_Projekat.dto.impl.TipDogadjajaDto;
import njt.NJT_Projekat.service.TipDogadjajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipovi-dogadjaja")
@CrossOrigin(origins = "http://localhost:3000")
public class TipDogadjajaController {

    private final TipDogadjajaService tipDogadjajaService;

    @Autowired
    public TipDogadjajaController(TipDogadjajaService tipDogadjajaService) {
        this.tipDogadjajaService = tipDogadjajaService;
    }

    // GET http://localhost:8080/api/tipovi-dogadjaja
    @GetMapping
    public ResponseEntity<List<TipDogadjajaDto>> vratiSve() {
        return ResponseEntity.ok(tipDogadjajaService.vratiSve());
    }

    // POST http://localhost:8080/api/tipovi-dogadjaja
    @PostMapping
    public ResponseEntity<TipDogadjajaDto> kreiraj(@RequestBody TipDogadjajaDto tipDogadjajaDto) {
        return ResponseEntity.ok(tipDogadjajaService.kreiraj(tipDogadjajaDto));
    }
}